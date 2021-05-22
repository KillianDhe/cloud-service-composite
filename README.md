URLS :

- ShoppingService (GAE) : https://shopping-dot-projetcloud-313614.ew.r.appspot.com
- StockService (GAE) : https://stock-dot-projetcloud-313614.ew.r.appspot.com
- WholesalerService (Herkou) : https://shielded-earth-62387.herokuapp.com

Services :

- GAE : ShoppingService + StockService
- Heroku : WholesalerService

Base de données :

- Heroku : base PostgreSQL accessible directement depuis tous les services.
Chacun des services effectue des requêtes directement sur la base hébergée sur Heroku.

Séparation des tâches :

- Killian Dherment : Service ShoppingService + Service StockService + mise en place des exceptions détaillées
- Clément Maisonhaute : Service WholesalerService + Création et connexion à la base de donnée PostgreSQL + clients Guzzle
