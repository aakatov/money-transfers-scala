# REST API
* [Create Account](#create)
* [Show Account](#show)
* [Delete Account](#delete)
* [Transfer](#transfer)

<a name="create"></a>
## Create Account 

Creates a new account. The account ID is assigned by the system.  

* **URL**

  /accounts

* **Method:**

  `POST`
  
*  **URL Params**

   None

* **Data Params**

    ```
  {
    "clientName": [string],
    "amount": [decimal]
  }
    ```

* **Success Response:**
  
  Returns a created account

  * **Code:** 201 Created<br />
    **Content:** 
    ```
       {
         "id": [long],
         "clientName": [string],
         "amount": [decimal]
       }
    ```
 
* **Error Response:**

  * **Code:** 400 Bad Request <br />
    **Content:** `Client name is required`

  OR

  * **Code:** 400 Bad Request <br />
    **Content:** `Amount is required`

* **Sample Call:**
  ```
  curl -H "Content-Type: application/json" -d '{"clientName":"Richard Hendricks","amount":1000000}' http://localhost:8080/accounts
  ``` 
<a name="show"></a>
## Show Account 

* **URL**

  /accounts/:id

* **Method:**

  `GET`
  
*  **URL Params**

   None

* **Data Params**

   None

* **Success Response:**
  
  * **Code:** 200 OK <br />
    **Content:** 
    ```
       {
         "id": [long],
         "clientName": [string],
         "amount": [decimal]
       }
    ```
 
* **Sample Call:**
  ```
  curl http://localhost:8080/accounts/1
  ``` 
<a name="delete"></a>
## Delete Account 

* **URL**

  /accounts/:id

* **Method:**

  `DELETE`
  
*  **URL Params**

   None

* **Data Params**

   None

* **Success Response:**
  
  * **Code:** 204 No Content<br />
 
* **Sample Call:**
  ```
    curl -X DELETE http://localhost:8080/accounts/1
  ``` 

<a name="transfer"></a>  
## Transfer Money 

Transfers money between two accounts. 

* **URL**

  /accounts/:id/transfer

* **Method:**

  `POST`
  
*  **URL Params**

   None

* **Data Params**

    ```
  {
    "targetId": [long],
    "amount": [decimal]
  }
    ```

* **Success Response:**
  
  Returns the source account

  * **Code:** 200 OK<br />
    **Content:** 
    ```
       {
         "id": [long],
         "clientName": [string],
         "amount": [decimal]
       }
    ```
 
* **Error Response:**

  * **Code:** 400 Bad Request <br />
    **Content:** `Incorrect amount: [amount]`

  OR

  * **Code:** 400 Bad Request <br />
    **Content:** `Source and target accounts must be different`

  OR

  * **Code:** 400 Bad Request <br />
    **Content:** `Insufficient funds on account [accountId]. Current balance is [amount]`    

* **Sample Call:**
  ```
  curl -H "Content-Type: application/json" -d '{"targetId":2,"amount":100}' http://localhost:8080/accounts/1/transfer
  ``` 