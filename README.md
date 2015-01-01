RandomQuadTreeGenerator1M
=========================

This Tool is for generatation spatial data and indexing, creating, partitioning and loading data into impala table. 

###In side details

#####This tool Do:

1. Generate uniform random spatial data.
2. Build Quad tree Index on top of the Generated data.
3. Annotate the Gen data using the index (as a tag partitioning column for Impala usage)
4. Store Index information (ID , MBR , Tag ) on "points_global_index" and
store statistical usage data (ID, count hits) on "index_count.csv".
5. Stored Partitioned data files inside data directory named "points_id.csv" where id is the partation id.
6. Generate SQL file to create, Partition and load data on table "points".
7. Generate bash script file to load data and index into HDFS and run the SQL file generated.

#####how to use:

1. Run the tool.
2. Run the generated bash script.
