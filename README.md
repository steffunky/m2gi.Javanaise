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
cd build
java jvn.coordinator.Program &
```

### Lancement du client IRC
```sh
cd build
java irc.Irc &
```
