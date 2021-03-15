# byteman-example

This repo shows how to use Byteman. [Byteman](https://byteman.jboss.org/) is a tool which makes it easy to trace, monitor and test the behaviour of Java application and JDK runtime code, and it can be used for injecting fault to Java application in Chaos Engineering.


## Install Byteman

Download binary and document:

```bash
wget https://downloads.jboss.org/byteman/4.0.14/byteman-download-4.0.14-bin.zip
```

Uncompress the zip file:

```bash
unzip byteman-download-4.0.14-bin.zip
```

Set the environment variable:

```bash
export BYTEMAN_HOME=`pwd`/byteman-download-4.0.14
export PATH=$PATH:${BYTEMAN_HOME}/bin
```

## Build example Java application

Enter into the directory of example:

```bash
cd example.helloworld
```

Build the Java code:
```bash
javac HelloWorld/Main.java
jar cfme Main.jar Manifest.txt HelloWorld.Main HelloWorld/Main.class
```

You can run the application by executing the command below:

```bash
java -jar Main.jar
```

The output looks like:

```log
0. Hello World
1. Hello World
...
```

Exit this directory:

```bash
cd ...
```

## Using Byteman

### Prepare Byteman script

You can see there are three files under directory `rules`:

```bash
$ ls -l rules 
total 24
-rw-r--r--  1 xiang  staff  100  3 15 17:09 return.btm
-rw-r--r--  1 xiang  staff  137  3 15 17:09 throw.btm
-rw-r--r--  1 xiang  staff  107  3 15 17:10 trace.btm
```

`return.btm` modifies the return value of function `getnum`.

`throw.btm` throw an exception for function `sayhello`.

`trace.btm` prints a trace log for function `sayhello`.

### Run Byteman

There are two ways to run Byteman.

1. Start Example with Byteman as javaagent

```bash
java -javaagent:./byteman-download-4.0.14/lib/byteman.jar=script:rules/throw.btm  -jar ./example.helloworld/Main.jar
```

The output looks like this:

```log
Got an exception!java.io.IOException: BOOM
Got an exception!java.io.IOException: BOOM
Got an exception!java.io.IOException: BOOM
```

You can change the `throw.btm` to `return.btm` or `trace.btm` to see the output.

2. Attach Byteman

Using this way can modify the bytecode of an existing Java process.

First, you need to run the example Java application:

```bash
java -jar ./example.helloworld/Main.jar
```

Then get the PID of the process.

Attach Byteman into JVM:

```bash
bminstall.sh -b -Dorg.jboss.byteman.transform.all -Dorg.jboss.byteman.verbose ${PID}
```

Install rules:

```bash
bmsubmit.sh -l rules/throw.btm
```

The output looks like this:

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

Uninstall rules:
```bash
bmsubmit.sh -u rules/throw.btm
```

You can change the `throw.btm` to `return.btm` or `trace.btm` to see the output.