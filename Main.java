package testThree;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {
    static byte rows, columns, size;
    static byte[][]next;
    static int[][]manhattanDistanceArray;
    static long startTime;
    static int nodesExpanded = 0;

    public static void main(String[] args) throws Exception {
        final Output out = new Output(System.out); //create an object to call later on to write into the file.
        byte[] initialBoard =
                        {8,7,6,
                        5,4,3,
                        2,1,0};
        Node initial;
        rows = 3;
        columns = 3;
        size = (byte)(rows * columns); // == 9
        next = new byte[size][];
        for (int i = 0; i < size; i++) {
            initialBoard[i]--;
        }
        for(int i=0;i<size;i++){  //checks for Moves available
            int s = 4;
            int r = i/columns;
            int c = i%columns;
            if(r==0||(r+1==rows))s--;
            if(c==0||(c+1==columns))s--;
            next[i] = new byte[s];
            if(r > 0){
                next[i][--s]=(byte)(((r-1)*columns) + c);
            }
            if(c > 0){
                next[i][--s]=(byte)((r*columns) + c-1);
            }
            if(r+1 < rows){
                next[i][--s]=(byte)(((r+1)*columns) + c);
            }
            if(c+1 < columns){
                next[i][--s]=(byte)((r*columns) + c+1);
            }
        }
        manhattanDistanceArray = new int[size][size]; //array for manhattan distance
        for(int i=0;i<size;i++){
            manhattanDistanceArray[i][i]=0;
        }
        for(int i=0;i<size;i++){ //manhattan distance calculation
            for(int j=i+1;j<size;j++){
                manhattanDistanceArray[i][j]=manhattanDistanceArray[j][i]=
                        (Math.abs((i / columns) - (j / columns)) + Math.abs((i % columns) - (j % columns)));
            }
        }
        initial = new Node(initialBoard);

        if(!isPossible(initial)){
            System.out.println("This board has no solution.");
        }else{
            System.out.println("Choose '1' for A* Search and '2' for Uniform Cost Search"); //Uniform cost doesn't work
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String choice = scanner.nextLine();
                if (choice.equals("1")) {
                    startTime = System.currentTimeMillis();
                    calculateAstar(initial);
                    break;
                } else if (choice.equals("2")) {
                    startTime = System.currentTimeMillis();
                    calcUCS(initial);
                    break;
                } else {
                    System.err.println("Error: Choose 1 or 2!");
                    continue;
                }
            }
            while(initial != null){
                out.printLn(">> Step "+initial.getMoves());
                out.printLn(initial);
                out.printLn("Cost / g(n): " + initial.getCost() + " Heuristic / h(n): " + initial.countInversions() + " f(n): " + (initial.getCost()+initial.countInversions()));
                System.out.println(">>Step " + initial.getMoves());
                System.out.println(initial);
                System.out.println("Cost / g(n): " + initial.getCost() + " Heuristic / h(n): " + initial.countInversions() + " f(n): " + (initial.getCost()+initial.countInversions()));
                initial = initial.next;
            }
        }
        System.err.println("Time: " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("Nodes Expanded: " + nodesExpanded);
        out.printLn("Time: " + (System.currentTimeMillis() - startTime) + "ms");
        out.printLn("Nodes Expanded: " + nodesExpanded);
        out.close();
    }
    // ( (grid width odd) && (inversions even) )  ||  ( (grid width even) && ((blank on odd row from bottom) == (inversions even)) )
    static boolean isPossible(Node b){ //Checks for legality of an initial state
        int inversions = (b.countInversions() & 1);
        if((columns & 1) == 1){
            return (inversions & 1) == 0;
        }
        int row = (rows - b.getCurId()/columns);
        return (row & 1) != (inversions & 1);
    }
    static void calcUCS(Node initial) {

    }
    static void calculateAstar(Node initial){
        PriorityQueue<Node> pq = new PriorityQueue<>(); //create an empty list UnexpNodes;
        HashSet<Node> set = new HashSet<>();
        pq.add(initial); //Add initial node to UnexpNodes;
        set.add(initial);
        while (!pq.isEmpty()) { //WHILE (UnexpNodes not empty)
            Node cur = pq.poll(); //N = The item n in UnexpNodes with g(n) = h(n) being smallest //Remove N from UnexpNodes
            if(cur.isGoal()){ //If N is goal
                while(cur != initial){
                    cur.previous.next = cur;
                    cur = cur.previous;
                }//THEN Produce Solution and Return(Success)
                break;
            }
            nodesExpanded++;
            for(byte i=0;i<next[cur.getCurId()].length;i++){//Expand N to produce list of successors of N;
                if(cur.getCurId() == next[cur.getCurId()][i])continue; //IF cheaper routes to any node in UnexpNodes have not been found continue;
                Node nextBoard = new Node(cur, next[cur.getCurId()][i]); //Else Then replace their coslier parent nodes with the cheaper ones;
                if(set.add(nextBoard)){
                    pq.add(nextBoard); //add successors to tail of UnexpNodes;
                }
            }
        }
    }
}