import java.util.Scanner;

public class bankFinder {
    static branch branchRoot;
    static bank bankRoot;
    static hash bankHash =new hash();
    static hash neighbourhoodHash=new hash();

    public static void main(String[] args) {
        Scanner input=new Scanner(System.in);

        String order=input.next();
        while (!order.equals("end")){
            switch (order){
                case "addN":
                    System.out.println("enter name of the neighbourhood:");
                    String name=input.next();
                    System.out.println("enter 4 points for the coordinate:(8 numbers)");
                    int[][] cor={{input.nextInt() , input.nextInt()} ,{input.nextInt() , input.nextInt()},{input.nextInt() , input.nextInt()},{input.nextInt() , input.nextInt()}};
                    addN(name , cor);
                    break;
                case "addB":
                    System.out.println("enter the name of the bank:");
                    name=input.next();
                    System.out.println("enter a point for the coordinate:(2 numbers)");
                    int[] c={input.nextInt() , input.nextInt()} ;
                    System.out.println("enter number of bank's branches:");
                    int branches=input.nextInt();
                    if (contains(branchRoot , c, 0)||contains(bankRoot , c , 0))
                        System.out.println("theres already a bank here!");
                    else
                        addB(name , branches , c);
                    break;
                case "addBr":
                    System.out.println("enter the name of the branch:");
                    name=input.next();
                    System.out.println("enter the name of its bank:");
                    String bn=input.next();
                    System.out.println("enter a point for its coordinate:(2 numbers)");
                    c= new int[]{input.nextInt(), input.nextInt()};
                    if (contains(branchRoot , c, 0)||contains(bankRoot , c, 0))
                        System.out.println("theres already a bank here!");
                    else
                        addBr(bn ,name , c);
                    break;
                case "delBr":
                    System.out.println("enter the coordinate:");
                    c=new int[]{input.nextInt() , input.nextInt()};
                    if (contains(branchRoot , c,0))
                        System.out.println("there is no branch in this coordinate");
                    else
                        delBr(branchRoot ,c, 0);
                    break;
                case "listB":
                    System.out.println("enter the name of the neighborhood:");
                    name=input.next();
                    listB(name);
                    break;
                case "listBr":
                    System.out.println("enter the name of the bank:");
                    name=input.next();
                    listBrs(name);
                    break;
                case "nearB":
                    System.out.println("enter the coordinate you are at:(2 numbers)");
                    c=new int[]{input.nextInt() , input.nextInt()};
                    nearB(c);
                    break;
                case "nearBr":
                    System.out.println("enter the coordinate you are at:(2 numbers)");
                    c=new int[]{input.nextInt() , input.nextInt()};
                    System.out.println("enter the name of the bank:");
                    name=input.next();
                    nearBr(c , name);
                    break;
                case "availB":
                    System.out.println("enter the coordinate you are at:(2 numbers)");
                    c=new int[]{input.nextInt() , input.nextInt()};
                    System.out.println("enter r:");
                    int r=input.nextInt();
                    availB(c,r);
                    break;
            }
            order=input.next();
        }

    }
    static void addN(String name , int[][] coordinate){
        neighbourhood n=new neighbourhood(name , coordinate);
        neighbourhoodHash.put(name , n);
    }
    static void addB(String name, int numOfBranches, int[] coordinate){
        insert(bankRoot , name ,numOfBranches, coordinate , 0);
    }
    static void addBr(String bankName , String branchName, int[] coordinate){
        if (bankHash.get(bankName)==null)
            System.out.println("there is no bank with given name!");
        else
        insert(branchRoot ,bankName , branchName , coordinate, 0);
    }
    static branch delBr(branch branchRoot ,int[] coordinate , int depth){

        if (branchRoot == null)
            return null;

        boolean same = false;
        if (branchRoot.coordinate[0]==coordinate[0] && branchRoot.coordinate[1]==coordinate[1])
            same=true;
        if (same)
        {
            bank b= (bank) bankHash.get(branchRoot.bankName);
            branch[] temp=new branch[b.numOfBranches];
            int shift=0;
            for (int i=0 ;i<b.numOfBranches ; i++ ){
                if (b.branches[i].branchName.equals(branchRoot.branchName)) {
                    b.branches[i] = null;
                    shift=i;
                    break;
                }
                temp[i]=b.branches[i];
            }
            for (int i=shift ; i<temp.length-1 ; i++){
                temp[i]=b.branches[i+1];
            }
            b.branches=temp;
            if (branchRoot.right != null)
            {
               branch min = findMin(branchRoot.right, depth%2 , 0);
               branchRoot=min;
               branchRoot.right = delBr(branchRoot.right, min.coordinate, depth+1);
            }
            else if (branchRoot.left != null)
            {
                branch min= findMin(branchRoot.left, depth%2 , 0);
                branchRoot=min;
                branchRoot.left = delBr(branchRoot.left, min.coordinate, depth+1);
            }
            else
            {
                return null;
            }
            return branchRoot;
        }

        if (coordinate[depth%2] < branchRoot.coordinate[depth%2])
            branchRoot.left = delBr(branchRoot.left, coordinate, depth+1);
        else
            branchRoot.right = delBr(branchRoot.right, coordinate, depth+1);
        return branchRoot;
    }
    static void listB(String name){
        neighbourhood n= (neighbourhood) neighbourhoodHash.get(name);
        searchBankInNeighbourhood(bankRoot , n , 0);
        searchBranchInNeighbourhood(branchRoot , n ,0);
    }
    static void searchBankInNeighbourhood(bank bankRoot ,neighbourhood n , int depth ){
        if (bankRoot==null)
            return;
        if (n.inOrOut(bankRoot.coordinate)){
            System.out.println("bank name: "+bankRoot.name+" number of branches: "+bankRoot.numOfBranches+" coordinate:"+bankRoot.coordinate[0]+","+bankRoot.coordinate[1]);
        }
        if (depth%2==0){
            if (bankRoot.coordinate[0]<n.x1){
                searchBankInNeighbourhood(bankRoot.right , n , depth+1);
            }
            else if (bankRoot.coordinate[0]>n.x2){
                searchBankInNeighbourhood(bankRoot.left , n , depth+1);
            }
            else{
                searchBankInNeighbourhood(bankRoot.right , n , depth+1);
                searchBankInNeighbourhood(bankRoot.left , n , depth+1);
            }
        }
        if (depth%2==1){
            if (bankRoot.coordinate[1]<n.y1){
                searchBankInNeighbourhood(bankRoot.right , n , depth+1);
            }
            else if (bankRoot.coordinate[1]>n.y2){
                searchBankInNeighbourhood(bankRoot.left , n , depth+1);
            }
            else{
                searchBankInNeighbourhood(bankRoot.right , n , depth+1);
                searchBankInNeighbourhood(bankRoot.left , n , depth+1);
            }
        }
    }
    static void searchBranchInNeighbourhood(branch branchRoot , neighbourhood n , int depth){

        if (branchRoot==null)
            return;
        if (n.inOrOut(branchRoot.coordinate)){
            System.out.println("branch name: "+branchRoot.branchName+" bank name: "+branchRoot.bankName+" coordinate:"+branchRoot.coordinate[0]+","+branchRoot.coordinate[1]);
        }
        if (depth%2==0){
            if (branchRoot.coordinate[0]<n.x1){
                searchBranchInNeighbourhood(branchRoot.right , n , depth+1);
            }
            else if (branchRoot.coordinate[0]>n.x2){
                searchBranchInNeighbourhood(branchRoot.left , n , depth+1);
            }
            else{
                searchBranchInNeighbourhood(branchRoot.right , n , depth+1);
                searchBranchInNeighbourhood(branchRoot.left , n , depth+1);
            }
        }
        if (depth%2==1){
            if (branchRoot.coordinate[1]<n.y1){
                searchBranchInNeighbourhood(branchRoot.right , n , depth+1);
            }
            else if (branchRoot.coordinate[1]>n.y2){
                searchBranchInNeighbourhood(branchRoot.left , n , depth+1);
            }
            else{
                searchBranchInNeighbourhood(branchRoot.right , n , depth+1);
                searchBranchInNeighbourhood(branchRoot.left , n , depth+1);
            }
        }
    }
    static void listBrs(String bankName){
        bank b= (bank) bankHash.get(bankName);
        if (b.branches[0]==null) {
            System.out.println("no branch");
            return;
        }
        int i=0;
        while (i<b.branches.length && b.branches[i]!=null){
            System.out.print(b.branches[i].coordinate[0]+","+b.branches[i].coordinate[1]+" ");
            i++;
        }
        System.out.println();


    }
    static void nearB( int[] coordinate){
        bank b=nearest(bankRoot , bankRoot , coordinate,0);
        branch br=nearest(branchRoot , branchRoot , coordinate ,0);
        double d=distance(b.coordinate , coordinate);
        double dr=distance(br.coordinate , coordinate);
        if (d<0)
            d*=-1;
        if (dr<0)
            dr*=-1;
        if (d<dr)
            System.out.println("bank name: "+b.name+" number of branches: "+b.numOfBranches+" coordinate:"+b.coordinate[0]+","+b.coordinate[1]);
        else
            System.out.println("branch name: "+br.branchName+" bank name: "+br.bankName+" coordinate:"+br.coordinate[0]+","+br.coordinate[1]);
    }


    static void nearBr(int[] coordinate , String bankName){
        branch b=nearest(branchRoot , null , coordinate,bankName ,0);
        System.out.println("branch name: "+b.branchName+" bank name: "+b.bankName+" coordinate:"+b.coordinate[0]+","+b.coordinate[1]);
    }
    static void availB(int[] coordinate , int r){
        availBanks(bankRoot , coordinate , r);
        availBranch(branchRoot , coordinate , r);
    }
    static void availBanks(bank root,int[] coordinate , int r ){
        if (root==null)
            return;
        if (distance(coordinate , root.coordinate)<=r*r){
            System.out.println("bank name: "+root.name+"bank coordinate: "+root.coordinate[0]+","+root.coordinate[1]);
            availBanks(root.left , coordinate , r);
            availBanks(root.right , coordinate , r);
        }
        if (distance(coordinate , root.coordinate)>r*r){
            availBanks(root.left , coordinate , r);
        }
    }
    static void availBranch(branch root,int[] coordinate , int r ){
        if (root==null)
            return;
        if (distance(coordinate , root.coordinate)<=r*r){
            System.out.println("branch name: "+root.branchName+"branch coordinate: "+root.coordinate[0]+","+root.coordinate[1]);
            availBranch(root.left , coordinate , r);
            availBranch(root.right , coordinate , r);
        }
        if (distance(coordinate , root.coordinate)>r*r){
            availBranch(root.left , coordinate , r);
        }
    }
    static branch newNode(String bn, String brn, int[] arr)
    {
         branch temp = new branch(bn , brn , arr);
         if (branchRoot==null)
             branchRoot=temp;
        bank b= (bank) bankHash.get(bn);
        b.branches[b.count++]=temp;
        for (int i=0; i<2; i++)
            temp.coordinate[i] = arr[i];

        temp.left = temp.right = null;
        return temp;
    }

    static branch insert(branch root, String bn, String brn, int[] point, int depth)
    {
        if (root == null)
            return newNode(bn , brn , point);

        if (point[depth % 2] < (root.coordinate[depth % 2]))
            root.left = insert(root.left , bn , brn, point, depth + 1);
        else
            root.right = insert(root.right,bn , brn, point, depth + 1);

        return root;
    }
    static bank newNode(String bn, int nb, int[] arr)
    {
        bank temp = new bank(bn  ,nb, arr);
        if (bankRoot==null)
            bankRoot=temp;
        bankHash.put(bn , temp);
        for (int i=0; i<2; i++)
            temp.coordinate[i] = arr[i];

        temp.left = temp.right = null;
        return temp;
    }

    static bank insert(bank root, String bn, int nb, int[] point, int depth)
    {
        if (root == null)
            return newNode(bn  ,nb, point);


        if (point[depth % 2] < (root.coordinate[depth % 2]))
            root.left = insert(root.left , bn ,nb, point, depth + 1);
        else
            root.right = insert(root.right,bn , nb, point, depth + 1);

        return root;
    }

    static double distance(int [] p1 , int[] p2){
        return ((p1[0]-p2[0])*(p1[0]-p2[0]))+((p1[1]-p2[1])*(p1[1]-p2[1]));
    }
    static bank nearest(bank root, bank nearest,int[] target, int depth) {
        if (root==null)
            return nearest;

        if (distance(root.coordinate, target) < distance(nearest.coordinate , target)){
            nearest=root;
        }
        int d;
        if (depth%2==0)
            d=target[0]-root.coordinate[0];
        else
            d=target[1]-root.coordinate[1];
        if (d<0){
            nearest=nearest(root.left , nearest , target , depth+1);
            if (distance(nearest.coordinate , target)>=d*d)
                nearest=nearest(root.right , nearest , target , depth+1);
        }
        else {
            nearest = nearest(root.right, nearest, target, depth + 1);
            if (distance(nearest.coordinate , target)>=d*d)
                nearest=nearest(root.left , nearest , target , depth+1);
        }
        return nearest;
    }

    static branch nearest(branch root, branch nearest,int[] target,String name, int depth) {
        if (root==null)
            return nearest;

        if (nearest==null)
            if (root.bankName.equals(name))
                nearest=root;
        else {
                if (distance(root.coordinate, target) < distance(nearest.coordinate, target) && root.bankName.equals(name)) {
                    nearest = root;
                }
            }
        int d;
        if (depth%2==0)
            d=target[0]-root.coordinate[0];
        else
            d=target[1]-root.coordinate[1];
        if (d<0){
            nearest=nearest(root.left , nearest , target,name , depth+1);
            if (distance(nearest.coordinate , target)>=d*d)
                nearest=nearest(root.right , nearest , target , name,depth+1);
        }
        else {
            nearest = nearest(root.right, nearest, target,name, depth + 1);
            if (distance(nearest.coordinate , target)>=d*d)
                nearest=nearest(root.left , nearest , target ,name, depth+1);
        }
        return nearest;
    }

    static branch nearest(branch root, branch nearest,int[] target, int depth) {
        if (root==null)
            return nearest;

        if (distance(root.coordinate, target) < distance(nearest.coordinate , target)){
            nearest=root;
        }
        int d;
        if (depth%2==0)
            d=target[0]-root.coordinate[0];
        else
            d=target[1]-root.coordinate[1];
        if (d<0){
            nearest=nearest(root.left , nearest , target , depth+1);
            if (distance(nearest.coordinate , target)>=d*d)
                nearest=nearest(root.right , nearest , target ,depth+1);
        }
        else {
            nearest = nearest(root.right, nearest, target, depth + 1);
            if (distance(nearest.coordinate , target)>=d*d)
                nearest=nearest(root.left , nearest , target , depth+1);
        }
        return nearest;
    }

    static branch findMin(branch root, int d, int depth)
    {
        if (root == null)
            return null;

        if (depth%2 == d)
        {
            if (root.left == null)
                return root;
            return min(root,  findMin(root.left , d , depth+1) , d);
        }

        return min(root,
                findMin(root.left, d, depth+1),
                findMin(root.right, d, depth+1) , d);
    }
    static branch min(branch a, branch b , int d){
        if (d==0){
            if(a.coordinate[0]<b.coordinate[0])
                return a;
        }
        else {
            if(a.coordinate[1]<b.coordinate[1])
                return a;
        }
        return b;
    }
    static branch min(branch a, branch b ,branch c, int d){
        return min(a, min(b, c, d) , d);
    }
    static boolean contains(branch root, int[] point, int depth)
    {
        if (root == null)
            return false;
        if (root.coordinate[0]==point[0] && root.coordinate[1]==point[1])
            return true;

        if (point[depth%2] < root.coordinate[depth%2])
            return contains(root.left, point, depth + 1);

        return contains(root.right, point, depth + 1);
    }
    static boolean contains(bank root, int[] point, int depth)
    {
        if (root == null)
            return false;
        if (root.coordinate[0]==point[0] && root.coordinate[1]==point[1])
            return true;

        if (point[depth%2] < root.coordinate[depth%2])
            return contains(root.left, point, depth + 1);

        return contains(root.right, point, depth + 1);
    }
}

class neighbourhood{
    String name;
    int[][] coordinate;
    int x1, x2 , y1, y2;
    neighbourhood(String name , int[][] coordinate){
        this.name=name;
        this.coordinate=coordinate;
        if (coordinate[0][0]==coordinate[1][0]) {
            x1 = coordinate[0][0];
            y1=coordinate[0][1];
            y2=coordinate[1][1];
            x2=coordinate[2][0];
        }
        if (coordinate[0][0]==coordinate[2][0]) {
            x1 = coordinate[0][0];
            y1 = coordinate[0][1];
            y2 = coordinate[2][1];
            x2=coordinate[1][0];
        }
        if (coordinate[0][0]==coordinate[3][0]) {
            x1 = coordinate[0][0];
            y1 = coordinate[0][1];
            y2 = coordinate[3][1];
            x2=coordinate[1][0];
        }


        if (x1>x2){
            int temp=x1;
            x1=x2;
            x2=temp;
        }
        if (y1>y2){
            int temp=y1;
            y1=y2;
            y2=temp;
        }
    }
    boolean inOrOut(int[] cor){
        if ((cor[0]<this.x1 && cor[0]>this.x2) || (cor[0]>this.x1 && cor[0]<this.x2))
            return (cor[1] < this.y1 && cor[1] > this.y2) || (cor[1] > this.y1 && cor[1] < this.y2);

        return false;
    }
}
class bank{
    String name;
    int[] coordinate;
    int numOfBranches;
    int count=0;
    branch[] branches;
    bank left , right;
    bank(String name ,int numOfBranches, int[] coordinate){
        this.name=name;
        this.numOfBranches=numOfBranches;
        this.coordinate=coordinate;
        branches=new branch[numOfBranches];
    }
}
class branch {
    String bankName , branchName;
    int[] coordinate;
    branch left , right; //for tree
    branch(String bankName , String branchName , int[] coordinate){
        this.bankName=bankName;
        this.branchName=branchName;
        this.coordinate=coordinate;
    }
}
class hash{
    int size=7;
    entry[] data;
    hash(){
        data=new entry[size];
    }
    class entry{
        String key;
        Object value;
        entry next;

        entry(String key , Object value){
            this.key=key;
            this.value=value;
        }
    }
    public void put(String key , Object value){
        int index=getIndex(key);
        entry e=new entry(key , value);
        if (data[index]==null)
            data[index]=e;
        else{
            entry entries=data[index];
            while (entries.next!=null)
                entries=entries.next;
            entries.next=e;
        }
    }
    public Object get(String key){
        int index=getIndex(key);
        entry entries=data[index];
        if (entries!=null){
            while (!entries.key.equals(key) && entries.next!=null)
                entries=entries.next;
            return entries.value;
        }
        return null;
    }
    int getIndex(String key){
        int hashcode=key.hashCode();
        int index=hashcode % size;
        if (index<0)
            index+=size;
        return index;
    }
}