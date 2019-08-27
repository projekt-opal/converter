package org.diceresearch.qualitymetricsservice.utility.civet;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.diceresearch.qualitymetricsservice.utility.civet.CivetApi;
import org.diceresearch.qualitymetricsservice.utility.civet.exceptions.CivetException;

/**
 * Civet interface for the command line.
 * 
 * @author Adrian Wilke
 */
public class CivetCli {

	public static void main(String[] args) {
		CivetCli commandLineInterface = new CivetCli();

		// Missing arguments
		if (args.length == 0) {
			commandLineInterface.printHelp();
			return;
		} else {

			// Parse arguments
			CommandLine commandLine = null;
			try {
				commandLine = new DefaultParser().parse(commandLineInterface.getOptions(), args);
			} catch (ParseException e) {
				System.err.println("Error: " + e.getMessage());
				commandLineInterface.printHelp();
				return;
			}

			// Configure endpoints
			org.diceresearch.qualitymetricsservice.utility.civet.CivetApi civetApi = new CivetApi();
			try {
				civetApi.setSparqlQueryEndpoint(commandLine.getOptionValue(CivetCli.OPTION_ENDPOINT_QUERY));
				civetApi.setSparqlUpdateEndpoint(commandLine.getOptionValue(CivetCli.OPTION_ENDPOINT_UPDATE));
			} catch (CivetException e) {
				System.err.println("Exception during computation: " + e.toString());
			}
			if (commandLine.hasOption(CivetCli.OPTION_GRAPH)) {
				civetApi.setNamedGraph(commandLine.getOptionValue(CivetCli.OPTION_GRAPH));
			}

			// Run
			long time = System.currentTimeMillis();
			int offset = Integer.parseInt(commandLine.getOptionValue(CivetCli.OPTION_OFFSET));
			int end = -1;
			if (commandLine.hasOption(CivetCli.OPTION_END)) {
				end = Integer.parseInt(commandLine.getOptionValue(CivetCli.OPTION_END));
			}
			int limit = LIMIT_DEFAULT;
			if (commandLine.hasOption(CivetCli.OPTION_LIMIT)) {
				limit = Integer.parseInt(commandLine.getOptionValue(CivetCli.OPTION_LIMIT));
			}
			try {
				civetApi.computeAll(offset, end, limit);
			} catch (CivetException e) {
				System.err.println("Exception during computation: " + e.toString());
			}
			time = System.currentTimeMillis() - time;
			System.out.println("Runtime in seconds: " + (time / 1000));
		}
	}

	public static final String INFO = "Civet: OPAL quality framework";
	public static final String CMD_LINE_SYNTAX = "java -jar civet.jar";

	// Value of 1000 produced errors in {@link OpalAccessor#writeMetricResults(Map)}
	public static final int LIMIT_DEFAULT = 800;

	public final static String OPTION_ENDPOINT_QUERY = "q";
	public final static String OPTION_ENDPOINT_UPDATE = "u";
	public final static String OPTION_GRAPH = "g";

	public final static String OPTION_OFFSET = "o";
	public final static String OPTION_END = "e";
	public final static String OPTION_LIMIT = "l";

	public Options getOptions() {
		Options options = new Options();
		options.addOption(Option.builder(OPTION_ENDPOINT_QUERY).longOpt("query")
				.desc("SPARQL query endpoint, mandatory").hasArg().argName("URL").required().build());
		options.addOption(Option.builder(OPTION_ENDPOINT_UPDATE).longOpt("update")
				.desc("SPARQL update endpoint, mandatory").hasArg().argName("URL").required().build());
		options.addOption(Option.builder(OPTION_GRAPH).longOpt("graph").desc("(Named graph, optional)").hasArg()
				.argName("graph").build());

		options.addOption(Option.builder(OPTION_OFFSET).longOpt("offset")
				.desc("Offset for results (not datasets), mandatory").hasArg().argName("int").required().build());
		options.addOption(Option.builder(OPTION_END).longOpt("end")
				.desc("(Maximum number of results (not datasets), optional)").hasArg().argName("int").build());
		options.addOption(Option.builder(OPTION_LIMIT).longOpt("limit")
				.desc("(Number of results per iteration, optional)").hasArg().argName("int").build());
		return options;
	}

	public void printHelp() {
		PrintWriter writer = new PrintWriter(System.out);
		HelpFormatter formatter = new HelpFormatter();

		// Workaround for sorting arguments
		class OptionComparator implements Comparator<Option>, Serializable {
			private static final long serialVersionUID = 1L;

			public int compare(Option opt1, Option opt2) {
				List<String> list = new LinkedList<>();
				list.add(OPTION_ENDPOINT_QUERY);
				list.add(OPTION_ENDPOINT_UPDATE);
				list.add(OPTION_GRAPH);
				list.add(OPTION_OFFSET);
				list.add(OPTION_END);
				list.add(OPTION_LIMIT);
				return Integer.compare(list.indexOf(opt1.getOpt()), list.indexOf(opt2.getOpt()));
			}
		}
		formatter.setOptionComparator(new OptionComparator());

		formatter.printHelp(writer, formatter.getWidth(), CMD_LINE_SYNTAX, INFO, getOptions(),
				formatter.getLeftPadding(), formatter.getDescPadding(), "");
		writer.flush();
		writer.close();
	}
}