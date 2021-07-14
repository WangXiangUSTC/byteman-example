## Build example Java application

Build the Java code:
```bash
javac HelloWorld/Main.java
javac HelloWorld/Info.java
jar cfm Main.jar Manifest.txt HelloWorld/Main.class HelloWorld/Info.class
```

You can run the application by executing the command below:

```bash
java -jar Main.jar
```

The output looks like:

```log
0. 2021-07-14T11:56:22.351522 Hello World
1. 2021-07-14T11:56:23.392638 Hello World
2. 2021-07-14T11:56:24.397636 Hello World
3. 2021-07-14T11:56:25.401557 Hello World
...
```

## Using Byteman

### Prepare Byteman script

bminstall.sh -b -Dorg.jboss.byteman.transform.all -Dorg.jboss.byteman.verbose -p 9288 63694


You can see there are two files under directory `rules`:

```bash
$ ls -l rules 
total 16
-rw-r--r--  1 xiang  staff  465  7 14 11:53 return1.btm
-rw-r--r--  1 xiang  staff  194  7 14 11:55 return2.btm
```

`return1.btm` create a new info, and then return it in function `getInfo`.

`return2.btm` get the return value of function `getInfo`, and then modify it's time by call function `setTime`.

### Run Byteman

First, you need to run the example Java application:

```bash
java -jar ./Main.jar
```

Then get the PID of the process.

Attach Byteman into JVM:

```bash
bminstall.sh -b -Dorg.jboss.byteman.transform.all -Dorg.jboss.byteman.verbose -p 9288 ${PID}
```

Install rules 1:

```bash
bmsubmit.sh -p 9288 -l ./rules/return1.btm
```

The output looks like this:

```log
Rule.execute called for trace sayhello_0:0
trace sayhello execute
caught ReturnException
9999. 2017-02-13T15:56 Hello Chaos
```

Uninstall rules:
```bash
bmsubmit.sh -p 9288 -u ./rules/return1.btm
```

Install rules 2:

```bash
bmsubmit.sh -p 9288 -l ./rules/return2.btm
```

The output looks like this:

```log
Rule.execute called for trace sayhello_1:0
trace sayhello execute
351. 2017-02-13T15:56 Hello World
```

Uninstall rules:
```bash
bmsubmit.sh -p 9288 -u ./rules/return2.btm
```


