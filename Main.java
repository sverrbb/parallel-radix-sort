import java.util.Arrays;
import java.util.Scanner;

/**
 * Main class - measure and test algorithms
 */
class Main{

  static int n = 0;
  static int k = 0;
  static int runs = 8;
  static int seed = 3450;
  static int useBits = 7;

  //Font colors used in testing feedback
  public static final String GREEN = "\u001B[32m";
  public static final String RESET = "\u001B[0m";
  public static final String RED = "\u001B[31m";

  public static void main(String[] args){

      //Checks that number of arguments is right
      if(args.length != 2){
          System.out.println("The program requires 2 arguments");
          System.out.println("java Main <n> <number of threads>");
          return;
      }

      //Try to take input from command line and assign to variables
      try {
          n = Integer.parseInt(args[0]);
          k = Integer.parseInt(args[1]);
      } catch(Exception e){
          System.out.println("Error! Could not read input arguments");
      }

      //Number of threads equal to number of cores if k is 0
      if(k == 0) {
          k = Runtime.getRuntime().availableProcessors();
      }

      //Modes for method timeMeasurements
      int timeRuns = 1;
      int timeMedian = 2;

      Scanner input = new Scanner(System.in);
      showMenu();
      System.out.print("\nOption: ");
      int mode = input.nextInt();
      System.out.println();
      switch (mode) {
          case 1:
            System.out.print("\nChoose number of runs: ");
            runs = input.nextInt();
            timeMeasurements(timeRuns, n);
            break;
          case 2:
            timeMeasurementsForAllN(timeMedian);
            break;
          case 3:
            testProgram();
            break;
          default:
            System.out.println("Mode does not exist");
            break;
      }
  }



  /* Menu presented when user run the program */
  public static void showMenu(){
      System.out.println("\n**** MENU ****");
      System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
      System.out.println("1. Get measurements for each run");
      System.out.println("2. Get median measurements for all values of N");
      System.out.println("3. Run program tests");
  }



  /* Time measurements for all values of N */
  public static void timeMeasurementsForAllN(int timeMode){
      int allN[] = {1000, 10000, 100000, 1000000, 10000000, 100000000};
      for(int i = 0; i < allN.length; i++){
          timeMeasurements(timeMode, allN[i]);
          System.gc();
      }
  }



  /* Gets runtime for all algorithms */
  public static void timeMeasurements(int timeMode, int n){
      double[] seqRadixTimes = new double[runs];
      double[] parRadixTimes = new double[runs];

      for(int i = 0; i < runs; i++){

          // Time sequential radixSort
          RadixSort rs = new RadixSort(useBits);
          int[] unsorted_seq = Oblig4Precode.generateArray(n, seed);
          long start = System.nanoTime();
          int[] sortedArray = rs.radixSort(unsorted_seq);
          long end = System.nanoTime();
          double runtime = ((end - start) / 1000000.0);
          seqRadixTimes[i] = runtime;
          System.gc();

          // Time parallel radixSort
          int[] unsorted_para = Oblig4Precode.generateArray(n, seed);
          ParallelRadixSort prs = new ParallelRadixSort(unsorted_para, k, useBits);
          start = System.nanoTime();
          prs.radixSort();
          end = System.nanoTime();
          runtime = ((end - start) / 1000000.0);
          parRadixTimes[i] = runtime;
          System.gc();

      }
      // Prints measurements for each run
      if(timeMode == 1){
          getMeasurements(seqRadixTimes, parRadixTimes);
      }
      // Prints median measurements
      if(timeMode == 2){
          getMedianMeasurements(seqRadixTimes, parRadixTimes, n);
      }
  }



  /* Uses the runtime data to print median measurements */
  public static void getMedianMeasurements(double[] seqRadix, double[] parRadix, int n){

      // Sorts runtimes
      Arrays.sort(seqRadix);
      Arrays.sort(parRadix);

      // Finds median runtime RadixSort
      double seqMedian = seqRadix[runs/2];
      double parMedian = parRadix[runs/2];
      double speedup = seqMedian / parMedian;

      System.out.println("      **** MEDIAN MEASUREMENTS ****   ");
      System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
      System.out.println("N = " + n + " | Runs = " + runs + " | Threads = " + k);
      System.out.println();

      // Prints out runtime and speedup
      System.out.println("Radix Sort");
      System.out.println("• Sequential median: " + seqMedian + " ms");
      System.out.println("• Parallel median: " + parMedian + " ms");
      System.out.println("• Speedup: " + speedup);
      System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
      System.out.println("\n");
  }



  /* Uses runtime data to print measurements for each run */
  public static void getMeasurements(double[] seqRadix, double[] parRadix){
      for(int i = 0; i < runs; i++){
          System.out.println("Run nr. " + (i+1));
          System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
          printRuntime("Sequentual Radix Sort", "Parallel Radix Sort", seqRadix[i], parRadix[i]);
          System.out.println();
      }
  }



  /* Prints runtime and speedup */
  public static void printRuntime(String name1, String name2, double runtimeSeq, double runtimePar){
      System.out.println("• Runtime " + name1 + ": " + runtimeSeq + " ms");
      System.out.println("• Runtime " + name2 + ": " + runtimePar + " ms");
      System.out.println("• Speedup: " + runtimeSeq/runtimePar);
      System.out.println();
  }



  /* Run all program tests */
  public static void testProgram(){
      System.out.println("**** PROGRAM TESTS ****");
      System.out.println("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
      System.out.println("Test for N = " + n + "\n");

      // Sequential
      RadixSort rs = new RadixSort(useBits);
      int[] unsorted_seq = Oblig4Precode.generateArray(n, seed);
      int[] seq = rs.radixSort(unsorted_seq);

      // Parallel
      int[] para = Oblig4Precode.generateArray(n, seed);
      new ParallelRadixSort(para, k, useBits).radixSort();

      // Program tests
      checkSort(seq, "sequential"); //Check correct sort sequential version
      checkSort(para, "parallel"); //Check correct sort parallel version
      compareOutput(seq, para); //Chekck that output is the same

      Oblig4Precode.saveResults(Oblig4Precode.Algorithm.SEQ, seed, seq);
      Oblig4Precode.saveResults(Oblig4Precode.Algorithm.PAR, seed, para);

      System.out.println();
  }



  /* Check that array is sorted in correct order */
  public static void checkSort(int[] results, String name) {
      for(int i = 0; i < results.length-1; i++){
          if(results[i] > results[i+1]){
                System.out.println("Status order test: [" + RED +  "failed" + RESET + "]");
                System.out.println(" - Index: " + i + " (" + results[i] +  ") > Index: " + (i+1) + " (" + results[i+1] + ")");
                return;
            }
        }
        System.out.println("Status order test " + name + ": [" + GREEN +  "passed" + RESET + "]");
  }



  /* Check that sequential and parallel version generated same output */
  public static void compareOutput(int[] seq, int[] para){
        if (!Arrays.equals(seq, para)){
            System.out.println("Status compare test: [" + RED +  "failed" + RESET + "]");
            return;
        } else {
            System.out.println("Status compare test: [" + GREEN +  "passed" + RESET + "]");
        }
    }
}
