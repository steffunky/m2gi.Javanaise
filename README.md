# Javanaise

#### Compiler le projet
```sh
mkdir build
javac -d build/ $(find ./src -name "*.java")
```


#### Lancement du registre RMI
```sh
cd build
rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false &
```

#### Lancement du coordinateur
```sh
java build.jvn.coordinator.Program &
```

### Lancement du client IRC
```sh
java build.irc.Irc &
```
