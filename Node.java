package testThree;

import java.util.Arrays;

import static testThree.Main.manhattanDistanceArray;

public class Node implements Comparable<Node> {
    private final byte[]state;
    private final byte previousId;
    private byte currentId;
    private final int movements, hash;
    private byte missPlaced;
    private int cost; // g(n)
    private int count = 0;
    Node next = null, previous = null; //setting both Current and Previous node to null.
    /**CONSTRUCTOR*/
    public Node(byte[] blocks) { // initialBoard is passed into the constructor from main method
        state = new byte[9]; //empty state with a length of 9
        this.previousId = this.currentId = -1; //assigning -1 to both current and previous node IDs
        this.movements = 0; // When the program runs, the first step is 0 because it shows initial Node.
        cost = missPlaced = 0; //setting cost and heuristic to 0;
        for (byte i = 0; i < 9; i++) {
            this.state[i] = blocks[i]; //state array is initialBoard
            if (state[i] == -1) { //this line checks where the zero is located in the array
                this.currentId = i; //zero is -1 because we I subtracted all the values in an array by 1 in main method. So to say -1 is location of 0
            }else{
                if (MissPlaced(i)) {
                    missPlaced++;
                }
                cost += manhattan(i);
            }
        }
        for(int r=0;r<3;r++){ //Linear conflict for a row
            cost += linearConflictRow(r);
        }
        for(int c=0;c<3;c++){ //Linear conflict for a column
            cost += linearConflictColumn(c);
        }
        // HASH
        hash = Arrays.hashCode(this.state);
    }
    /**Args CONSTRUCTOR*/
    public Node(Node previous, byte newId) {
        this.previous = previous;
        this.state = new byte[9];
        for (byte i = 0; i < 9; i++) {
            this.state[i] = previous.state[i];
        }
        this.movements = previous.movements + 1;
        this.missPlaced = previous.missPlaced;
        this.cost = previous.cost;
        this.previousId = previous.currentId;
        this.currentId = newId;
        swapPrevious();
        hash = Arrays.hashCode(this.state);
    }
    private void swapPrevious() {
        this.cost++; //calculates the cost
        if (MissPlaced(this.currentId)) {
            missPlaced--;
        }
        /**calculates manhattan distance before the changes*/
        this.cost -= manhattan(this.currentId);
        int r1 = this.currentId/3, // new row
                r2 = this.previousId/3, // old row
                r3 = this.state[this.currentId]/3; // row that got tile moved
        int	c1 = this.currentId%3, // new column
                c2 = this.previousId%3, // old column
                c3 = this.state[this.currentId]%3; // column that got tile moved
        /**checks for linear conflict before the changes*/
        if(c1 != c2
                && (c1 == c3 || c2 == c3)){
            this.cost -= linearConflictColumn(c3);
        }else if(r1 != r2
                && (r1 == r3 || r2 == r3)){
            this.cost -= linearConflictRow(r3);
        }
        /**Start: swapping*/
        byte t = this.state[currentId];
        this.state[currentId] = this.state[previousId];
        this.state[previousId] = t;

        if (MissPlaced(this.previousId)) {
            missPlaced++;
        }
        /**calculates manhattan distance after the changes*/
        this.cost += manhattan(this.previousId);
        /**checks for linear conflict after the changes*/
        if(c1 != c2
                && (c1 == c3 || c2 == c3)){ // column change
            this.cost += linearConflictColumn(c3);
        }else if(r1 != r2
                && (r1 == r3 || r2 == r3)){ // row change
            this.cost += linearConflictRow(r3);
        }
    }
    private boolean MissPlaced(byte id) { //id is from 0 to 8
        return state[id] != id; //
    }
    private int manhattan(byte id) { //manhattan distance
        return manhattanDistanceArray[id][this.state[id]];
    }
    private int linearConflictRow(int row){ //linear conflict is combined with manhattan distance to get the heuristic value
        int max = -1;
        int res = 0;
        for(int c=0,id=row*3;c<3;c++,id++){
            if(state[id] == -1 || state[id]/3 != row)
                continue;
            if(max < state[id]){
                max = state[id];
            }else{
                res += 2;
            }
        }
        return res;
    }
    public int linearConflictColumn(int col){ //linear conflict is combined with manhattan distance to get the heuristic value
        int max = -1; //it is used for improving efficiency and timing for solving a puzzle
        int res = 0;
        for(int r=0,id=col;r<3;r++,id+=3){
            if(state[id] == -1 || state[id]%3 != col)
                continue;
            if(max < state[id]){
                max = state[id];
            }else{
                res += 2;
            }
        }
        return res;
    }
    public boolean isGoal() { //Method used for checking if the Node is equal to the Goal Node
        return missPlaced == 0;
    }
    @Override
    public boolean equals(Object y) { //checks if  this and that object are equal, in our case Nodes.
        Node that = (Node) y;
        return this.hash == that.hash
                && this.cost == that.cost
                && this.missPlaced == that.missPlaced
                && Arrays.equals(this.state, that.state);
    }
    public int countInversions(){ //Inversion count is for checking how far the array is from being sorted. if the array reaches Goal State, it is equal to zero.
        for(int i=0;i<9;i++){
            if(state[i]==-1)continue;
            for(int j=i+1;j<9;j++){
                if(state[j]==-1)continue;
                if(state[j]<state[i])
                    count++;
            }
        }
        return count;
    }
    public String toString() { //transforms 0 to X and adds +1 to every number in the puzzle, as -1 shows the location of 0
        StringBuilder s = new StringBuilder();
        int c = 0;
        for (byte i = 0; i < 9; i++) {
            if(state[i]==-1){
                s.append("   ");
            }else{
                s.append(String.format("%2d ", state[i]+1));
            }
            if(++c == 3){ //if 8 7 6 then produce new line ... 5 4 3 -> new line etc.
                c=0;
                s.append("\n");
            }
        }
        return s.toString();
    }
    @Override
    public int compareTo(Node o) { //compares Nodes
        if (this.cost == o.cost) {
            if(this.movements == o.movements){
                return this.hash < o.hash ? -1 : 1;
            }
            return this.movements > o.movements ? -1 : 1;
        }
        return this.cost < o.cost ? -1 : 1; //if current Node is lower than passed in Node then return -1, else 1
    }
    /*GETTERS*/
    public int getMoves() {
        return movements;
    }

    public byte getCurId() {
        return currentId;
    }

    public int getCost() {
        return cost;
    }
}