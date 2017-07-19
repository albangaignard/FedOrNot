import glob, ntpath, math
from snakemake.utils import R

GROUND_TRUTH = "training_data"
GROUND_TRUTH_FED = GROUND_TRUTH + "/fedQueryLogs"
GROUND_TRUTH_SINGLE = GROUND_TRUTH + "/singleQueryLogs"
MODELS = "models"
FEDORNOT_JAR = "lib/FedOrNot-0.1-SNAPSHOT-bin.jar"
WEKA_JAR = "lib/weka-3.6.12.jar"

inFedlogsBZ = glob.glob(GROUND_TRUTH_FED + "/*.log.bz2")
inSinglelogsBZ = glob.glob(GROUND_TRUTH_SINGLE + "/*.log.bz2")

inFedFiles = set()
inSingleFiles = set()

## extract file names from paths
for p in inFedlogsBZ:
	inFedFiles.add(os.path.basename(p).replace(".bz2",""))
for p in inSinglelogsBZ:
	inSingleFiles.add(os.path.basename(p).replace(".bz2",""))

rule all:
	input:
		dt = MODELS+"/decisionTree.model.log",
		rf = MODELS+"/randomForest.model.log"

rule bunzip2:
	input: GROUND_TRUTH+"/{dir}/{file}.log.bz2"
	output: GROUND_TRUTH+"/{dir}/{file}.log"
	shell: "bunzip2 -k {input}"

rule feature_extraction_single:
	input: GROUND_TRUTH_SINGLE+"/{file}"
	output: GROUND_TRUTH_SINGLE+"/{file}.arff"
	shell: "java -Xmx8g -jar "+ FEDORNOT_JAR +" -i {input} -v -l single -ls ? single fed -o {output} > {output}.error"

rule feature_extraction_fed:
	input: GROUND_TRUTH_FED+"/{file}"
	output: GROUND_TRUTH_FED+"/{file}.arff"
	shell: "java -Xmx8g -jar "+ FEDORNOT_JAR +" -i {input} -s -l fed -ls ? single fed -o {output} > {output}.error"

rule removeQueryFeature:
	input: GROUND_TRUTH+"/{dir}/{file}.arff"
	output: GROUND_TRUTH+"/{dir}/{file}.noquery.arff"
	shell: "java -cp "+ WEKA_JAR +" weka.filters.unsupervised.attribute.Remove -R 21 -i {input} -o {output}"

rule produceHeader:
	input: expand(GROUND_TRUTH_SINGLE+"/{f}.noquery.arff",f=list(inSingleFiles)[0])
	output: GROUND_TRUTH+"/header.txt"
	shell: "sed '1,26!d' {input} > {output}"

rule skipHeader:
	input: GROUND_TRUTH+"/{dir}/{file}.noquery.arff"
	output: GROUND_TRUTH+"/{dir}/{file}.noquery.noheader.arff"
	shell: "sed '1,26d' {input} > {output}"

rule mergeSingleFiles:
	input:
		i1 = GROUND_TRUTH+"/header.txt",
		i2 = expand(GROUND_TRUTH_SINGLE+"/{f}.noquery.noheader.arff",f=inSingleFiles)
	output: GROUND_TRUTH_SINGLE+"/all.arff"
	run:
		allfiles = " ".join(expand(GROUND_TRUTH_SINGLE+"/{f}.noquery.noheader.arff",f=inSingleFiles))
	 	cmd = "cat "+GROUND_TRUTH+"/header.txt "+ allfiles + " > {output}"
	 	shell(cmd)

rule mergeFedFiles:
	input:
		i1 = GROUND_TRUTH+"/header.txt",
		i2 = expand(GROUND_TRUTH_FED+"/{f}.noquery.noheader.arff",f=inFedFiles)
	output: GROUND_TRUTH_FED+"/all.arff"
	run:
		allfiles = " ".join(expand(GROUND_TRUTH_FED+"/{f}.noquery.noheader.arff",f=inFedFiles))
	 	cmd = "cat "+GROUND_TRUTH+"/header.txt "+ allfiles + " > {output}"
	 	shell(cmd)

rule randomize:
	input: GROUND_TRUTH+"/{dir}/all.arff"
	output: GROUND_TRUTH+"/{dir}/all.randomized.arff"
	shell: "java -cp "+ WEKA_JAR +" weka.core.Instances randomize 4 {input} > {output}"

rule assemble_ground_truth_50_50:
	input:
		singles = GROUND_TRUTH_SINGLE+"/all.randomized.arff",
		federated = GROUND_TRUTH_FED+"/all.randomized.arff"
	output:
		sub_singles = GROUND_TRUTH_SINGLE+"/sub_50_50.randomized.arff",
		gt = GROUND_TRUTH+"/ground_truth.arff"
	run:
		nb_fed = sum(1 for line in open(input.federated))
		nb_single = nb_fed
		cmd1 = "sed -n 27,"+ str(nb_single) + "p " + str(input.singles) + " > " + str(output.sub_singles)
		cmd2 = "cat "+str(input.federated)+ " "+str(output.sub_singles)+ " > " + str(output.gt)
		shell(cmd1)
		shell(cmd2)

#rule assemble_ground_truth_all_instances:
#	input:
#		singles = GROUND_TRUTH_SINGLE+"/all.randomized.arff",
#		federated = GROUND_TRUTH_FED+"/all.randomized.arff"
#	output:
#		gt = GROUND_TRUTH+"/ground_truth_all.arff",
#		singles_no_header = GROUND_TRUTH_SINGLE+"/all.randomized.noheader.arff"
#	run:
#		cmd1 = "sed  1,23d " + str(input.singles) + " > " + str(output.singles_no_header)
#		cmd2 = "cat "+str(input.federated)+ " "+str(output.singles_no_header)+ " > " + str(output.gt)
#		shell(cmd1)
#		shell(cmd2)

rule build_DT_Model:
	input: GROUND_TRUTH+"/ground_truth.arff"
	output:
		model = MODELS+"/decisionTree.model",
		log = MODELS+"/decisionTree.model.log"
	shell: "java -cp "+ WEKA_JAR +" weka.classifiers.trees.J48 -t {input} -split-percentage 66 -d {output.model} > {output.log}"

rule build_RF_Model:
	input: GROUND_TRUTH+"/ground_truth.arff"
	output:
		model = MODELS+"/randomForest.model",
		log = MODELS+"/randomForest.model.log"
	shell: "java -cp "+ WEKA_JAR +" weka.classifiers.trees.RandomForest  -t {input} -I 100 -K 20 -d {output.model} > {output.log}"
