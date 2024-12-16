# appium-testng-maven-fw
```bash
mvn -Dtest=FirstTest test
```

Check dependencies updates using TERMINAL
```bash
mvn versions:display-dependency-updates
```


## Elastic Stack Setup

### Elasticsearch
**Version**: `elasticsearch-8.17.0`
###
#### Start Elasticsearch
```bash
D:\ELK_Stack\elasticsearch-8.17.0\bin\elasticsearch.bat
```
###
#### Retrieve the Password
Take the password from the console after the first run, or reset it using the following command:
```bash
D:\ELK_Stack\elasticsearch-8.17.0\bin\elasticsearch-reset-password -u elastic
```

```bash
D:\ELK_Stack\logstash-8.17.0\bin\logstash -f D:\ELK_Stack\logstash-8.17.0\logstash.conf
```

###
### Logstash Configuration
Below is the `logstash.conf` configuration for setting up Logstash to receive logs and send them to Elasticsearch.

```plaintext
input {
  tcp {
    port => 5044
    codec => json_lines
  }
}
output {
  elasticsearch {
    hosts => ["https://127.0.0.1:9200"]
    user => "elastic"
    password => "YvLGUzpp=ifXQ6PdtgNt"
    ssl => true
    ssl_certificate_verification => true
    cacert => "D:/ELK_Stack/elasticsearch-8.17.0/config/certs/http_ca.crt"
  }
}
```

###
### Socket Appender (for Logstash) setup inside `log4j2.properties`
```plaintext
...
# Socket Appender (for Logstash)
appender.socket.type = Socket
appender.socket.name = LOGSTASH
appender.socket.host = localhost
appender.socket.port = 5044
appender.socket.protocol = TCP
appender.socket.layout.type = JsonLayout
appender.socket.layout.compact = true
appender.socket.layout.eventEol = true
...
```
###
### Kibana Setup
To start Kibana, run the following command:

```bash
D:/ELK_Stack/kibana-8.17.0/bin/kibana.bat
```

###
### Kibana Enrollment
Access Kibana using the following URL: [http://localhost:5601/?code=789340](http://localhost:5601/?code=789340)

When prompted, enter the enrollment token. Generate the token with the command below:
```bash
D:\ELK_Stack\elasticsearch-8.17.0\bin\elasticsearch-create-enrollment-token.bat --scope kibana
```

###
### Elasticsearch Security Configuration
The Elasticsearch security features have been automatically configured:

- **Authentication** is enabled.
- **Cluster connections** are encrypted.

###
#### Password for the `elastic` user:
```plaintext
AtDsBxcAsDL3A*yfcXZb
```

###
#### HTTP CA Certificate SHA-256 Fingerprint:
```plaintext
48de21a555a3d97b1a2b38b9adfb29cf43521c5df1d941b0d234b19758321bdf
```

###
#### Configure Kibana to Use This Cluster:
1. **Run Kibana** and click the configuration link in the terminal when Kibana starts.
2. **Copy the following enrollment token** and paste it into Kibana in your browser (valid for the next 30 minutes):
```plaintext
eyJ2ZXIiOiI4LjE0LjAiLCJhZHIiOlsiMTkyLjE2OC4wLjEwOjkyMDAiXSwiZmdyIjoiNDhkZTIxYTU1NWEzZDk3YjFhMmIzOGI5YWRmYjI5Y2Y0MzUyMWM1ZGYxZDk0MWIwZDIzNGIxOTc1ODMyMWJkZiIsImtleSI6IjJ3V3N4Wk1CdlhUaHpqYU92c0prOjBsV3lJOXVsUzAtenNDNFNnT1M4ZUEifQ==
```

###
#### Configure Other Nodes to Join This Cluster:
1. **On this node:**
    - Create an enrollment token with the following command:
      ```bash
      bin/elasticsearch-create-enrollment-token -s node
      ```
   - Uncomment the `transport.host` setting at the end of `config/elasticsearch.yml`.
   - Restart Elasticsearch.

2. **On other nodes:**
    - Start Elasticsearch using the following command with the enrollment token you generated:
      ```bash
      bin/elasticsearch --enrollment-token <token>
      ```
###
## Access Kibana
- Open your browser and navigate to: [http://localhost:5601](http://localhost:5601)
- Enter the enrollment token generated below.