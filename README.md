# Javanaise

**Version courante :** Javanaise 2.0

**Etat du projet :** Le coordinateur et les clients IRC fonctionnent correctement. La gestion des verrous est transparente pour les programmeurs. Actuellement, l'obtention ou la création de l'objet à placer dans le coordinateur distant est pris en charge par le proxy.

### Installation
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

### Exemple d'utilisation
#### Interface ISentence
```java
public interface ISentence 
{
	@JvnAction(actionType=ActionType.WRITE)
	public void write(String text);
	
	@JvnAction(actionType=ActionType.READ)
	public String read();
}
```
#### Client IRC
```java
// Obtient ou créer un objet sentence avec la clé RMI "IRC" (objet géré en arrière plan par un JvnObject)
ISentence s = (ISentence) JvnProxy.newInstance(new Sentence(), "IRC");

// Obtient le texte de l'objet Sentence (LockRead réalisé avant invocation, Unlock réalisé après invocation de la méthode "read")
String text = s.read();

// Met à jour le texte de l'objet Sentence (LockWrite avant invocation et Unlock après invocation de la méthode "write")
s.write("Message");
```
