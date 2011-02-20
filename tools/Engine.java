// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Engine.java

import java.io.*;
import java.util.*;

public class Engine {

    public Engine() {
    }

    public static void KillClients(List list) {
        Iterator iterator = list.iterator();
        do {
            if (!iterator.hasNext())
                break;
            Process process = (Process) iterator.next();
            if (process != null)
                process.destroy();
        } while (true);
    }

    public static boolean AllTrue(boolean aflag[]) {
        for (int i = 0; i < aflag.length; i++)
            if (!aflag[i])
                return false;

        return true;
    }

    public static void main(String args[]) {
        if (args.length < 4) {
            System.err.println("ERROR: wrong number of command-line arguments.");
            System.err.println("USAGE: engine map_file_name max_turn_time max_num_turns log_filename player_one player_two [more_players]");
            System.exit(1);
        }
        String s = args[0];
        //my hack
        //my hack
        int i = Integer.parseInt(args[1]);
        int j = Integer.parseInt(args[2]);
        String s1 = args[3];

        String[] players = {
                "java DecentAllRounder6",
//                "java DecentAllRounder4",
//                "java DecentAllRounder3",
//                "java StableMyBot",
//                "java SnipingBot2",
//                "java SnipingBot3",
//                "java MyBot",
//                "java RogueWithFirstMove",
//                "java RogueWithFirstMove2",
//                "java MyBotHalfFixedBot",
//                "java AttackingBot1",
//                "java KasupWBot",
//                "java -jar ../../example_bots/DualBot.jar",
//                "java -jar ../../example_bots/RageBot.jar",
                "java -jar ../../bots/mkemp.jar",
                "java -jar ../../bots/zvold.jar",
                "java -jar ../../bots/shiva.jar",
                "java -jar ../../bots/exaide.jar",
                "java -jar ../../bots/deccan.jar",
                "java -jar ../../bots/Manwe56.jar",
                "java -jar ../../bots/ZerlingRush.jar",
        };

        // hack 2
        for (int player1 = 0; player1 < 1; player1++) {
            float total = 0;
            for (int player2 = 0; player2 < players.length; player2++) {
                int playerOneWinCount = 0;
                if (player1 == player2) {
                    continue;
                }

                // my hack
                for (int maps = 1; maps < 101; maps++) {
                    String map = s + "/map" + maps + ".txt";
                    // my hack

                    Game game = new Game(map, j, 0, s1);
                    if (game.Init() == 0)
                        System.err.println((new StringBuilder()).append("ERROR: failed to start game. map: ").append(s).toString());
                    ArrayList arraylist = new ArrayList();
// hack 2
//        for(int k = 4; k < args.length; k++)
//        {
                    executeProcess(arraylist, players[player1]);
                    executeProcess(arraylist, players[player2]);
//        }

                    boolean aflag[] = new boolean[arraylist.size()];
                    for (int i1 = 0; i1 < arraylist.size(); i1++)
                        aflag[i1] = arraylist.get(i1) != null;

                    int j1 = 0;
                    for (; game.Winner() < 0; game.DoTimeStep()) {
                        for (int k1 = 0; k1 < arraylist.size(); k1++) {
                            if (arraylist.get(k1) == null || !game.IsAlive(k1 + 1))
                                continue;
                            String s3 = (new StringBuilder()).append(game.PovRepresentation(k1 + 1)).append("go\n").toString();
                            try {
                                java.io.OutputStream outputstream = ((Process) arraylist.get(k1)).getOutputStream();
                                OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
                                outputstreamwriter.write(s3, 0, s3.length());
                                outputstreamwriter.flush();
                                game.WriteLogMessage((new StringBuilder()).append("engine > player").append(k1 + 1).append(": ").append(s3).toString());
                            }
                            catch (Exception exception1) {
                                arraylist.set(k1, null);
                            }
                        }

                        StringBuilder astringbuilder[] = new StringBuilder[arraylist.size()];
                        boolean aflag1[] = new boolean[arraylist.size()];
                        for (int l1 = 0; l1 < arraylist.size(); l1++) {
                            astringbuilder[l1] = new StringBuilder();
                            aflag1[l1] = false;
                        }

                        for (long l2 = System.currentTimeMillis(); !AllTrue(aflag1) && System.currentTimeMillis() - l2 < (long) i;) {
                            int i2 = 0;
                            while (i2 < arraylist.size()) {
                                if (!aflag[i2] || !game.IsAlive(i2 + 1) || aflag1[i2])
                                    aflag1[i2] = true;
                                else
                                    try {
                                        for (InputStream inputstream = ((Process) arraylist.get(i2)).getInputStream(); inputstream.available() > 0;) {
                                            char c = (char) inputstream.read();
                                            if (c == '\n') {
                                                String s4 = astringbuilder[i2].toString();
                                                s4 = s4.toLowerCase().trim();
                                                game.WriteLogMessage((new StringBuilder()).append("player").append(i2 + 1).append(" > engine: ").append(s4).toString());
                                                if (s4.equals("go"))
                                                    aflag1[i2] = true;
                                                else
                                                    game.IssueOrder(i2 + 1, s4);
                                                astringbuilder[i2] = new StringBuilder();
                                            } else {
                                                astringbuilder[i2].append(c);
                                            }
                                        }

                                    }
                                    catch (Exception exception2) {
                                        System.err.println((new StringBuilder()).append("WARNING: player ").append(i2 + 1).append(" crashed.").toString());
                                        ((Process) arraylist.get(i2)).destroy();
                                        game.DropPlayer(i2 + 1);
                                        aflag[i2] = false;
                                    }
                                i2++;
                            }
                        }

                        for (int j2 = 0; j2 < arraylist.size(); j2++)
                            if (aflag[j2] && game.IsAlive(j2 + 1) && !aflag1[j2]) {
                                System.err.println((new StringBuilder()).append("WARNING: player ").append(j2 + 1).append(" timed out.").toString());
                                ((Process) arraylist.get(j2)).destroy();
                                game.DropPlayer(j2 + 1);
                                aflag[j2] = false;
                            }

                        j1++;
                        // my hack (commented below)
                        //System.err.println((new StringBuilder()).append("Turn ").append(j1).toString());
                    }

                    KillClients(arraylist);
                    if (game.Winner() > 0) {
                        if (game.Winner() == 1) {
                            playerOneWinCount += 2;
                            System.err.print("W");
                        } else {
                            System.err.print("L");
                        }
                    } else {
                        playerOneWinCount++;
                        System.err.print("D");
                    }
                    //System.out.println(game.GamePlaybackString());
                    //my hack
                }
                System.err.println(" Match Score - " + players[player1] + " vs " + players[player2] + " : " + playerOneWinCount / 2.0 + " to " + (200 - playerOneWinCount) / 2.0);
                total += playerOneWinCount;
                //my hack
            }
            System.err.println(" Total Score " + players[player1] + " - " + total);
        } // hack 2
    } // hack 2

    private static void executeProcess(ArrayList arraylist, String s2) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(s2);
        }
        catch (Exception exception) {
            process = null;
        }
        if (process == null) {
            KillClients(arraylist);
            System.err.println((new StringBuilder()).append("ERROR: failed to start client: ").append(s2).toString());
            System.exit(1);
        }
        arraylist.add(process);
    }
}
