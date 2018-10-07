import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SparkSession

object Q1And2 {
    
    val conf = new SparkConf().setAppName("My Application")
    val sc = new SparkContext(conf)
    
    // Question 1
    case class part(PARTKEY: Int,NAME: String,MFGR: String,BRAND: String,TYPE: String,SIZE: Int,CONTAINER: String,RETAILPRICE: Double,COMMENT: String) 
    case class partsupp(PARTKEY: Int,SUPPKEY: Int,AVAILQTY: Int,SUPPLYCOST: Int,COMMENT: String)
    
    val partFile=sc.textFile("/data-sets/tpch/data/PART").map(_.split(","))
    val partsuppFile=sc.textFile("/data-sets/tpch/data/PARTSUPP").map(_.split(","))
    
    // Join using RDD
    val part_record = partFile.map(x => (x(0).toInt, part(x(0).toInt,x(1).toString(),x(2).toString(),x(3).toString(),x(4).toString(),x(5).toInt,x(6).toString,x(7).toDouble,x(8).toString())))
    val partsupp_record= partsuppFile.map(x => (x(0).toInt,partsupp(x(0).toInt,x(1).toInt,x(2).toInt,x(3).toInt,x(4).toString())))
    val joined_rdd = part_record.join(partsupp_record)
     
    // Join using DataFrame   
    val mySpark = SparkSession
     .builder()
     .appName("My Application")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
    import mySpark.implicits._
    val partdf=part_record.toDF()
    val partsuppdf=partsupp_record.toDF()  
    val joineddf = partdf.as('a).join(partsuppdf.as('b), $"a.PARTKEY" === $"b.PARTKEY")
   
    // Join using DataSet
    val partds=partdf.as[part]
    val partsuppds=partsuppdf.as[partsupp]
    val joined = partds.joinWith(partsuppds, partds("PARTKEY") === partsuppds("id"))
    
    // Question 2
    partdf.createOrReplaceTempView("partView")
    partsuppdf.createOrReplaceTempView("partsuppView")
    val joinedBySqlDF=mySpark.sql("select * from  partView join partsuppView on partView.PARTKEY=partsuppView.PARTKEY")
    
    
}
