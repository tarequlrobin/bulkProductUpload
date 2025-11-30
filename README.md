# Bulk Product Upload System

A Spring Boot-based application that supports uploading multiple product images along with metadata (name, SKU, description, price) in a single API request.

This system is designed for scenarios such as e-commerce catalog onboarding or batch product updates.

## Features

✔ Bulk Upload Endpoint (`form-data`)  
✔ Upload multiple product images at once  
✔ Metadata JSON mapping each image file  
✔ Product information automatically stored in DB  
✔ File storage organized by Product ID  
✔ Automatically generated product image URL  
✔ REST API to fetch stored products  
✔ Serve images via HTTP  
✔ In-memory H2 database (no external DB required)

---

## Design Decisions

| Aspect | Rationale |
|--------|-----------|
| UUID-based Product ID | Prevent collisions and simplify folder-based file storage |
| File grouped by Product ID | Easier future deletion/update of product |
| ProductResponseDto | Clean API response separating entity from presentation |
| H2 in-memory DB | Simplifies testing & demo setup |
| Multipart JSON mapping | Ensures metadata-to-image mapping accuracy |

Example Storage Layout:

product-storage/
└── <product-id>/
├── product.json
└── product-image.jpg


## Technologies Used

- Java 17
- Spring Boot 3+
- Spring Web + JPA
- H2 Database
- Lombok
- Maven

---

## How to Run the Project

### **Clone the repository**
git clone https://github.com/tarequlrobin/bulkProductUpload.git
cd bulkProductUpload

Build
./mvnw clean package
(or mvn clean package if Maven is installed globally)

Run
./mvnw spring-boot:run
Server starts at:
http://localhost:8081

API Documentation

Bulk Upload Products
POST /api/products/bulk

Content-Type: multipart/form-data

Part	        Type	                  Description
files	      List<MultipartFile>	    Product images
metadatas	  String (JSON)	          Metadata array mapped by filename

Example for Postman — metadatas field (Text)
[
  {
    "filename": "shampoo.jpg",
    "meta": {
      "name": "Shampoo",
      "sku": "SKU001",
      "description": "Hair care product",
      "price": 199.99
    }
  },
  {
    "filename": "soap.jpg",
    "meta": {
      "name": "Soap Bar",
      "sku": "SKU002",
      "description": "Bathing soap",
      "price": 49.50
    }
  }
]
Add files via Postman → Bulk select images
Response Example:

[
  {
    "id": "e6c730e9-82a1-4a04-8ba3-676c3c41ee98",
    "name": "Shampoo",
    "sku": "SKU001",
    "description": "Hair care product",
    "price": 199.99,
    "imageUrl": "http://localhost:8081/images/<product-id>/shampoo.jpg"
  }
]

Fetch All Products
GET /api/products
Returns metadata + public image URLs.

Serve Image File
GET /images/{productId}/{fileName}
Displays the stored product image.

H2 Database Console
Visit:
http://localhost:8081/h2-console
JDBC URL:
jdbc:h2:mem:productsdb

Folder Storage Settings
Configured in application.properties:
product.storage.base-dir=./product-storage


👨‍💻 Author
S. M. Tarequl Hasan Robin
GitHub: @tarequlrobin
