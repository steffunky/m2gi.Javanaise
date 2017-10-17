# Javanaise

**Version courante :** Javanaise 1.0

**Etat du projet :** Le coordinateur et les clients IRC fonctionnent globalement correctement pour la V1.
Quelques petits problèmes persistent : tant que le premier client lancé (celui qui créer l'objet IRC) n'as pas write, le write des autres clients paraît inutile, il faut de ce fait commencer la transaction par une action du premier client.

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
