package pl.aetas.microservices.etcd

import groovy.json.JsonOutput
import mousio.etcd4j.EtcdClient
import mousio.etcd4j.responses.EtcdKeysResponse

class Reader {

    public static void main(String[] args) {
        OptionAccessor options = parseCliArgs(args)
        String baseKey = options.baseKey
        String etcdUrl = options.etcdUrl

        println "Using etcd server on: $etcdUrl"
        println "Using base key: $baseKey"

        List<EtcdKeysResponse.EtcdNode> nodes = []
        new EtcdClient(URI.create(etcdUrl)).withCloseable { etcd ->
            nodes = etcd.get(baseKey).recursive().send().get().node.nodes
        }

        def reader = new Reader()
        Map<String, Object> dataMap = reader.readNodes(nodes)
        String json = JsonOutput.prettyPrint(JsonOutput.toJson(dataMap))
        if (options.output) {
            def file = new File(options.output as String)
            file.write(json)
        } else {
            println json
        }
    }

    private static OptionAccessor parseCliArgs(String[] args) {
        def cli = new CliBuilder()
        cli.with {
            k longOpt: 'baseKey', args: 1, argName: 'base key',    'Path of the key used to be a starting point', required: true
            u longOpt: 'etcdUrl', args: 1, argName: 'etcd URL',    'URL use to connect to etcd server', required: true
            o longOpt: 'output',  args: 1, argName: 'output file', 'Path to the file where JSON output will be stored. Output only to standard output if not specified.'
        }
        return cli.parse(args)
    }

    public Map<String, Object> readNodes(List<EtcdKeysResponse.EtcdNode> nodes) {
        def map = [:]
        map.putAll(readKeyValues(nodes))
        map.putAll(readDirectories(nodes))
        return map
    }

    private Map<String, String> readKeyValues(List<EtcdKeysResponse.EtcdNode> nodes) {
        nodes.findAll { !it.dir } collectEntries { [getLastPartOfKey(it.key), it.value] }
    }

    private Map<String, Object> readDirectories(List<EtcdKeysResponse.EtcdNode> nodes) {
        nodes.findAll { it.dir } collectEntries { [getLastPartOfKey(it.key), readNodes(it.nodes)] }
    }

    private String getLastPartOfKey(String fullKey) {
        fullKey.substring(fullKey.lastIndexOf('/') + 1)
    }
}
