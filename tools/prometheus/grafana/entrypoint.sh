#!/bin/bash

echo "#ML.1 Start Grafana to be able to register my datasources..."
./run.sh "${@}" &
timeout 10 bash -c "until </dev/tcp/localhost/3000; do sleep 1; done"

#curl -s -H "Content-Type: application/json" \
#    -XPOST http://admin:secret@localhost:3000/api/datasources \
#    -d @- <<EOF
#{
#	"orgId": 1,
#	"name": "ES stagemonitor",
#	"type": "elasticsearch",
#	"typeLogoUrl": "",
#	"access": "proxy",
#	"url": "http://elasticsearch:9200",
#	"password": "",
#	"user": "",
#	"database": "[stagemonitor-metrics-]YYYY.MM.DD",
#	"basicAuth": false,
#	"basicAuthUser": "",
#	"basicAuthPassword": "",
#	"withCredentials": false,
#	"isDefault": false,
#	"jsonData": {
#		"esVersion": 5,
#		"interval": "Daily",
#		"timeField": "@timestamp",
#		"timeInterval": ">10s"
#	},
#	"secureJsonFields": {}
#}
#EOF

echo "#ML.2 Register my datasources..."
curl -s -H "Content-Type: application/json" \
    -XPOST http://admin:secret@localhost:3000/api/datasources \
    -d @- <<EOF
{
	"orgId": 1,
	"name": "Prometheus",
	"type": "prometheus",
	"url": "http://prometheus:9090",
	"access": "proxy",
	"isDefault": true
}
EOF

echo "#ML.3 Restarting Grafana to make my registrations to take effect..."
pkill grafana-server

echo "#ML.4 Wait for Grafana to shutdown..."
timeout 10 bash -c "while </dev/tcp/localhost/3000; do sleep 1; done"

echo "#ML.5 Start Grafana with my registrations..."
exec ./run.sh "${@}"