# etcd-config-reader
VERY basic tool to read data from [etcd](https://coreos.com/etcd/) datastore recursively and outputs it in JSON format.

## Usage
```
java -jar etcd-config-reader-1.0.0.jar -u <URI to etcd> -k <base key in etcd> [-o <outputFileName>]

 -k,--baseKey <base key>     Path of the key used to be a starting point
 -o,--output <output file>   Path to the file where JSON output will be
                             stored. Output only to standard output if not
                             specified.
 -u,--etcdUrl <etcd URL>     URL use to connect to etcd server
```

Example:

``java -jar etcd-config-reader-1.0.0.jar -u http://192.168.99.100:4001 -k mycompany/someKey``

