# Firebase Firestore Setup

Este projeto utiliza Firebase Firestore para sincronizar dados em tempo real entre dispositivos.

## Passos para configurar

### 1. Criar projeto Firebase

1. Aceda a [Firebase Console](https://console.firebase.google.com/)
2. Clique em "Adicionar projeto"
3. Siga os passos para criar um novo projeto

### 2. Registar a app Android

1. No painel do projeto Firebase, clique em "Adicionar app" > Android
2. Introduza o package name: `com.joao.warehouse`
3. Descarregue o ficheiro `google-services.json`
4. Substitua o ficheiro `app/google-services.json` pelo ficheiro descarregado

### 3. Ativar Firestore

1. No painel Firebase, va a "Firestore Database"
2. Clique em "Criar base de dados"
3. Selecione o modo de teste (para desenvolvimento) ou configure regras de seguranca

### 4. Regras de seguranca recomendadas

Para producao, configure as seguintes regras no Firestore:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /warehouses/{warehouseId}/{document=**} {
      allow read, write: if true;
    }
  }
}
```

Para maior seguranca, pode ativar autenticacao anonima e restringir o acesso:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /warehouses/{warehouseId}/{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 5. Estrutura dos dados no Firestore

```
warehouses/{warehouseId}/
  products/{productId}
    - id: number
    - name: string
    - description: string
    - sku: string
    - quantity: number
    - minStockLevel: number
    - categoryId: number (nullable)
    - location: string
    - barcode: string
    - imageUri: string (nullable)
    - createdAt: number (timestamp millis)
    - updatedAt: number (timestamp millis)

  categories/{categoryId}
    - id: number
    - name: string
    - description: string

  movements/{movementId}
    - id: number
    - productId: number
    - type: string ("IN" ou "OUT")
    - quantity: number
    - reason: string
    - timestamp: number (timestamp millis)
```

## Como funciona

- Cada dispositivo introduz um "Codigo de Armazem" no ecra inicial
- Todos os dispositivos com o mesmo codigo partilham os mesmos dados
- As alteracoes sincronizam automaticamente em tempo real via Firestore snapshotListeners
- O Firestore tem persistencia offline integrada, pelo que a app funciona sem internet
- Quando a ligacao e restabelecida, os dados sincronizam automaticamente

## Nota importante

O ficheiro `app/google-services.json` incluido no repositorio contem valores placeholder.
A app vai compilar mas NAO vai ligar ao Firebase ate substituir o ficheiro por um valido
descarregado da Firebase Console.
