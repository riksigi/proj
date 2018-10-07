/*For frequent scans an alternate storage format would be Parquet.
 * Parquet allows compression,encoding,columnar projection,partition discovery etc. which improves the Spark work loads performance. 
 * The table can be partitioned based on the partition keys and each partition can be stored as a seperate folder under the root directory.
 * The files inside all partitions can be compressed with snappy which will save a lot of space.
 * At below there is a code example containing full scan operations with text and parquet format.
 * For measuring performance,one can comment out one format at a time and run in the spark-shell something like below depending on cluster :
 * $ spark-shell --num-executors 12 --executor-cores 4 --executor-memory 4g 
 * The Spark WebUI gives performance metrics like 
 * 1) Total Time Across All Tasks : xyz mins
 * 2) Input Size/Records : xxx GB/ yyyyyyy
 * 3) Duration : zzz min
 * The execution plan from an explain query OR from the webUI can also give information of the query plan
 */
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SparkSession
object Q3 {
 val conf = new SparkConf().setAppName("My Application")
 val sc = new SparkContext(conf)
 val mySpark = SparkSession
     .builder()
     .appName("My Application")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
    import mySpark.implicits._
 
    // COMMENT OUT ONE FORMAT,RUN THE OTHER FORMAT AND VICE VERSA TO MEASURE PERFORMANCE
    
 // Running full scan with parquet format   
 ///////////////////////////////////////////////////////////////////////////////////////
  val dfParquet = mySpark.read.format("parquet").load("/data-sets/tpch/data/PART")
  dfParquet.createOrReplaceTempView("partParquet")
  mySpark.sql("select * from partParquet")
 /////////////////////////////////////////////////////////////////////////////////////////
  
  // Running full scan with text format
 /////////////////////////////////////////////////////////////////////////////////////////
 val dfText= mySpark.read.csv("/data-sets/tpch/data/PART")
 dfText.createOrReplaceTempView("partText")
 mySpark.sql("select * from partText")
 ////////////////////////////////////////////////////////////////////////////////////////////
}