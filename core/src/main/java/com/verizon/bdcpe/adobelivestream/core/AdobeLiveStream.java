package com.verizon.bdcpe.adobelivestream.core;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.System.out;

/*
 * Created by Alvaro Muir<alvaro.muir@verizon.com>
 * Verizon Big Data & Cloud Platform Engineering
 * 6/24/17.
 */


public class AdobeLiveStream {
    private static final Logger log = LoggerFactory.getLogger(AdobeLiveStream.class);
    /* this is a toy test class that simply logs the stream to stdout */

    /**
     * Generate usage information with Apache Commons CLI.
     *
     * @param options Instance of Options to be used to prepare
     *    usage formatter.
     */
    private static void printUsage(final Options options)
    {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "adobelivestream-core";
        final PrintWriter pw  = new PrintWriter(out);
        formatter.printUsage(pw, 80, syntax, options);
        pw.flush();
    }


    /**
     * Generate help information with Apache Commons CLI.
     *
     * @param options Instance of Options to be used to prepare
     *    help formatter.

     */
    private static void printHelp(final Options options)
    {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "adobelivestream-core <parameters>";
        final String usageHeader = "Parameters";
        final String usageFooter = "http://...";
        out.println("\nadobelivestream.core retrieves real-time event data from Adobe Analytics.");
//        printUsage(options);
        formatter.printHelp(syntax, usageHeader, options, usageFooter);
    }


    public static void main(final String[] args) throws Exception {

        Credentials credentials;
        final Options options = new Options();

        options.addOption("k", "appKey", true, "application key (or clientId)");
        options.addOption("s", "appSecret", true, "application secret (or client secret)");
        options.addOption("i", "appId", true, "applicationId");
        options.addOption("m", "connectionsMax", true, "maximum connection limits");
        options.addOption("o", "OauthTokenUrl", true, "[opt] Adobe OAuth 2.0 token request url");
        options.addOption("h", "proxyHost", true, "[opt] proxy host, if required");
        options.addOption("n", "proxyportNumber", true, "[opt] proxy port number, if required");
        options.addOption("u", "proxyUsername", true, "[opt] proxy username, if required");
        options.addOption("p", "proxyPassword", true, "[opt] proxy password, if required");
        options.addOption("l", "eventLimit", true,"[opt] limit of retrieved events");
        options.addOption("?", "help", false, "prints help information");

        final CommandLineParser parser = new DefaultParser();

        try {
            final CommandLine cmd = parser.parse(options, args);
            HelpFormatter formatter = new HelpFormatter();


            final String key = getOption('k', cmd);
            final String secret = getOption('s', cmd);
            final String id = getOption('i', cmd);
            final Integer maxConnections = cmd.hasOption('m') ? Integer.parseInt(getOption('m', cmd)): 0;
            final String OauthTokenUrl = cmd.hasOption('o') ? getOption('o', cmd):"https://api.omniture.com/token";
            final String proxyHost = getOption('h', cmd);
            final Integer proxyportNumber = cmd.hasOption('n') ? Integer.parseInt(getOption('n', cmd)) : 80;
            final String proxyUsername = getOption('u', cmd);
            final String proxyPassword = getOption('p', cmd);
            final Integer eventLimit = cmd.hasOption('l') ? Integer.parseInt(getOption('l', cmd)) : 0;

            if (cmd.hasOption('?') || cmd.hasOption("help")) {
                printHelp(options);
            } else {
                if(cmd.hasOption('k') && cmd.hasOption('s') && cmd.hasOption('i') && cmd.hasOption('m')) {
                    Credentials.Builder cb = new Credentials.Builder(key, secret).tokenRequestUrl(OauthTokenUrl);
                    if(cmd.hasOption('h')) {
                        cb.proxyHost(proxyHost);
                        cb.proxyPortNumber(proxyportNumber);
                        cb.proxyUserName(proxyUsername);
                        cb.proxyPassword(proxyPassword);
                        credentials = cb.build();
                    } else { credentials = cb.build(); }

                    BlockingQueue<String> events = new LinkedBlockingQueue<>();
                    Connection connection = new Connection.Builder(
                            credentials,
                            new Endpoint.Builder(id, maxConnections).build(),
                            new TokenRequest(credentials).newToken()
                    )
                            .eventQueue(events)
                            .build();
                    Thread producer = new Thread(connection);
                    producer.start();
                    if(eventLimit > 0) {
                        int limit = eventLimit;
                        String hit = "";
                        while(limit > 0) {
                            try { hit = events.take(); } catch (Exception e) { log.error(e.getMessage());}
                            out.println(hit);
                            limit -= 1;
                        }
                    } else {
                        //noinspection InfiniteLoopStatement
                        while(true) {

                            try {
                                out.println(events.take());
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        }
                    }
                    connection.stop();
                } else { formatter.printHelp( "adobelivestream", options ); }
            }
        }
        catch( ParseException e ) {
            String errMsg = "An error has occurred: " + e.getMessage();
            printUsage(options);
            log.error(errMsg);
            throw new Exception(errMsg);
        }
    }

    private static String getOption(final char option, final CommandLine commandLine) {
        if (commandLine.hasOption(option)) {
            return commandLine.getOptionValue(option);
        }
        return StringUtils.EMPTY;
    }
}
