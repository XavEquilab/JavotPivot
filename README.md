# JavotPivot

JavotPivot is the new Pivot

# TEST

Pour déployer un pylier tournant sur l'ordinateur:

-> avoir un appareil connecté a un moxa connecté à l'ordinateur sur un reseau ip d'adresse 192.168.200.*** (l'interface moxa est disponible sur http://192.168.200.1/)

-> dans les dossiers du pylier, lancer main.py (l'interface pylier est disponible sur http://localhost:1300/)

Pour lancer l'hyperviseur en local:

-> dans un terminal a la racine du projet vsc : php artisan serve (l'interface hyperviseur est disponible sur http://localhost:8000/ id:user0 mdp:password)

Pour le module test, envoyer la commande ci-dessous:

-> moduleId=1002&command=set_reset_soft&value=1234&composant=Envoi


# BUID AND RUN

Dans la console vsc:

-> mvn spring-boot:run
