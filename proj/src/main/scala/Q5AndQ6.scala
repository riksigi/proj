import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._

object Q5AndQ6{
  val mySpark = SparkSession
     .builder()
     .appName("My Application")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
 
 val conf = new SparkConf().setAppName("My Application")
 val sc = new SparkContext(conf)  
  
  val tableDF=mySpark.read
  .option("header","true")
  .option("inferSchema","true")
.csv("/path/to/fileContainingData.csv")
.withColumn("month_col", month(col("date"))) // adding an extra column month_col used for partitioning 
.withColumn("year_col", year(col("date"))) // adding an extra column year_col also used for partitioning 
    

tableDF.write
.mode(SaveMode.Overwrite)
.format("parquet")
.option("compression","snappy")
.option("path","/data-sets/tpch/data/external_table")
.partitionBy("month_col","year_col")
.saveAsTable("rubi.external_table_partitioned")
  
// caching
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
 sqlContext.cacheTable("rubi.external_table_partitioned")
 
 /////////////////////// Question 6////////////////////////////////////////////////////////////////////////
 val deltaDF=mySpark.read
 .option("header","true")
  .option("inferSchema","true")
.csv("/path/to/deltaFile.csv")
.withColumn("month_col", month(col("date"))) // adding an extra column month_col used for partitioning 
.withColumn("year_col", year(col("date")))// adding an extra column year_col also used for partitioning

 mySpark.conf.set(
  "spark.sql.sources.partitionOverwriteMode", "dynamic"
)

deltaDF.write
.mode(SaveMode.Overwrite)
.partitionBy("month_col","year_col")
.insertInto("rubi.external_table_partitioned")


}