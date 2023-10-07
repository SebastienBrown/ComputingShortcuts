public class PiThreads implements Runnable {
  
  //creates class attributes for PiThread
  private int id;        //thread id
  private float[][] matrix;  //the 2D network representation matrix we wish to transform
  private int size;  //size of the 2D matrix
  private float[][] shortcutsT;  //the 2D matrix storing the transpose of the source array
  private int startRow;   //first row the thread reads from the source matrix
  private int endRow;   //used to determine how many lines are read by each thread
  private float[][] results;  //shortcut matrix R


  //creates a class constructor for the PiThread class
// specifies the size of the matrix to transform, the id of each thread, the input 2D network representation matrix,
// the transposed shortcutsT matrix, the delimiting rows startRow and endRow, and the results 2D float array
public PiThreads (int size,int id, float[][] matrix,float[][] shortcutsT,int startRow, int endRow, float[][]results) {
  this.size=size;
  this.id = id;
  this.matrix=matrix;
  this.shortcutsT=shortcutsT;
  this.startRow=startRow;
  this.endRow=endRow;
  this.results=results;
}

public void run(){

//loops through endRow-startRow rows of the source array
for (int i=startRow;i<endRow;++i){

  //for loop iterates through each element of a given row
  for (int j=0;j<size;++j){

        //sets min to be the smallest possible Java value
        float min=Float.MAX_VALUE;

        //implements the getShortcutMatrix algorithm used in the Baseline implementation
        for(int k=0;k<size;++k){
          float x=matrix[i][k];
          float y=shortcutsT[j][k];
          float z=x+y;
          
          if(z<min){
            min=z;
          }

          if(min==0){
            break;
          }
        }
        //fills the specifed rows of the intermediate 2D array with the outputs of the shortcut distance procedure
        results[i][j]=min;

      }

    }
    
}
}
