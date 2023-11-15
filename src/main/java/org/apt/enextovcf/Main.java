package org.apt.enextovcf;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class Main{

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Create a new Options object to define the expected command line options
        Options options = createOptions();

        // Create the command line parser
        CommandLineParser parser = new DefaultParser();

        try {
            // Parse the command line arguments
            CommandLine cmd = parser.parse(options, args);

            // Retrieve the values of the positional arguments
            String[] positionalArgs = cmd.getArgs();
            if (positionalArgs.length < 2) {
                System.err.println("Missing required positional arguments: input file and output directory");
                printUsage(options);
                System.exit(1);
            }
            if (cmd.hasOption("h")) {
                printUsage(options);
                System.exit(0);
            }
            String inputFile = positionalArgs[0];
            String outputDirectory = positionalArgs[1];
            File outDir = new File(outputDirectory);
            if (!outDir.exists() || !outDir.isDirectory() ) {
                System.err.println("Directory does not exist " + outputDirectory);
                System.exit(1);
            }

            // Check if optional options are provided
            String outputCsvFile = cmd.getOptionValue("c");
            String singleFile = cmd.getOptionValue("s");

            List<EnexNote> enexNotes = EnexNote.parseEnexFile(inputFile);
            List<BusinessCard> bcards = enexNotes.stream().map(BusinessCard::new).toList();

            if (outputCsvFile != null) {
                CSVContent csv = new CSVContent(bcards);
                csv.writeCVS(new File(outDir, outputCsvFile));
            }
            List<EnexVCard> vcards = bcards.stream().map(EnexVCard::new).toList();
            if (singleFile != null) {
                File outVCFFile = new File(outDir, singleFile);
                try (BufferedWriter writer =
                             new BufferedWriter(new FileWriter(outVCFFile
                                     , StandardCharsets.UTF_8))) {
                    List<String> output = new LinkedList<>();
                    for (EnexVCard vcard : vcards)
                        output.add(vcard.writeString());
                    for (String s : output) {
                        writer.write(s);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    logger.error("IO Exception writing to " + outVCFFile);
                    System.exit(1);
                }
            } else {
                vcards.forEach(x -> x.write(outDir));
                if (cmd.hasOption('d')) vcards.forEach(x -> x.writeCardImage(outDir));
            }
        } catch (ParseException e) {
            System.out.println("Error parsing command line arguments: " + e.getMessage());
            printUsage(options);
        }



    }

    private static Options createOptions() {
        Options options = new Options();

        // Add the required positional arguments for input file and output directory

        // Add optional options for output CSV and single file with flags
        Option outputCsvOption = Option.builder("c")
                .argName("csvfile")
                .hasArg()
                .desc("Output to a CSV file with the given name")
                .build();
        Option singleFileOption = Option.builder("s")
                .argName("singlefile")
                .hasArg()
                .desc("Write all the cards into a single vcf file with the given name")
                .build();

        options.addOption(outputCsvOption);
        options.addOption(singleFileOption);

        Option helpOption = Option.builder("h")
                .argName("help")
                .desc("Print Help")
                .build();
        options.addOption(helpOption);

        Option cardImages = Option.builder("d")
                .argName("cardimages")
                .desc("Write Card Images separately to individual files in the directory")
                .build();
        options.addOption(cardImages);

        return options;
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Enex2vcard <inputfile> <outputdir> [-c --csvfile] [-s --singlefile]", options);
    }
}

