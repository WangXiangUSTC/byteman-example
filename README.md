# byte-monkey-example

## build example

```bash
cd example.helloworld
```

```bash
javac HelloWorld/Main.java
```

```bash
jar cfme Main.jar Manifest.txt HelloWorld.Main HelloWorld/Main.class
```

```bash
java -jar Main.jar
```

```log
Hello World
...
```

## Download byteman

https://byteman.jboss.org/downloads.html

wget https://downloads.jboss.org/byteman/4.0.14/byteman-download-4.0.14-bin.zip

export BYTEMAN_HOME=/Users/xiang/newWorld/gopath/src/github.com/chaos-mesh/byte-monkey-example/byteman-download-4.0.14
export PATH=$PATH:${BYTEMAN_HOME}/bin


## Using byteman

### Prepare btm config



### Start Example with byteman as javaagent

java -javaagent:./byteman-download-4.0.14/lib/byteman.jar=script:throw.btm  -jar ./example.helloworld/Main.jar

```log
Got an exception!java.io.IOException: BOOM
Got an exception!java.io.IOException: BOOM
Got an exception!java.io.IOException: BOOM
```


### Attach byteman

bminstall.sh -b -Dorg.jboss.byteman.transform.all -Dorg.jboss.byteman.verbose 85937


bmsubmit.sh -l throw.btm

```log
Hello World
TransformListener() : handling connection on port 9091
retransforming HelloWorld.Main
org.jboss.byteman.agent.Transformer : possible trigger for rule throw an exception at sayhello in class HelloWorld.Main
RuleTriggerMethodAdapter.injectTriggerPoint : inserting trigger into HelloWorld.Main.sayhello() void for rule throw an exception at sayhello
org.jboss.byteman.agent.Transformer : inserted trigger for throw an exception at sayhello in class HelloWorld.Main
Rule.execute called for throw an exception at sayhello_0:0
HelperManager.install for helper class org.jboss.byteman.rule.helper.Helper
calling activated() for helper class org.jboss.byteman.rule.helper.Helper
Default helper activated
calling installed(throw an exception at sayhello) for helper classorg.jboss.byteman.rule.helper.Helper
Installed rule using default helper : throw an exception at sayhello
throw an exception at sayhello execute
caught ThrowException
Got an exception!java.io.IOException: BOOM
Rule.execute called for throw an exception at sayhello_0:0
```

bmsubmit.sh -u throw.btm
