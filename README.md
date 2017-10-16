# Javanaise

**Version courante :** Javanaise 1.0

**Etat du projet :** Le coordinateur et les clients IRC disposent chacun d'une instance différente d'objet après lookup/register. Ainsi la communication IRC n'est pas fonctionnelle.

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

#### Lancement du client IRC
```sh
cd build
java irc.Irc &
```
