hadoop fs -mkdir /index
hadoop fs -put ./points_tb_global_index /index/
hadoop fs -mkdir /data
hadoop fs -put ./data/points_*.csv /data/
~/SpatialImpala/bin/impala_shell.sh -f ./startSQL.sql
