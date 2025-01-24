# Supera ENEM

## Documentação
https://www.notion.so/Supera-ENEM-154d2c4d9b8080179d4feddf7742bf87

## Migrações
```ssh 
mvn flyway:migrate
```

Se der erro:

```ssh 
mvn flyway:repair
mvn flyway:migrate -Dflyway.outOfOrder=true
```

## Acessar o Banco
```ssh
docker exec -it supera_enem_postgres psql -U NSmoJtFZRGitRofNFrUxNyaqGbRRbMdA enem
```

