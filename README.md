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

👨‍💻 Author
S. M. Tarequl Hasan Robin
GitHub: @tarequlrobin
