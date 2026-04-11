# BroSport E-commerce Platform

Hệ thống E-commerce bán lẻ đồ thể thao cao cấp (Giày, Quần áo, Phụ kiện) tích hợp các tính năng nổi bật như: Tìm kiếm thông minh bằng Elasticsearch, Cổng thanh toán VNPAY, Cloudinary (Lưu ảnh), và Web-socket Chat (Hỗ trợ trực tuyến thời gian thực).

---

## 🛠 Yêu Cầu Hệ Thống (Prerequisites)

Để chạy trơn tru dự án dưới Local, máy tính của bạn cần được cài đặt:
- **Java JDK 17** trở lên.
- **Maven 3.8+** (nếu không chạy trực tiếp qua Wrapper).
- **Docker Engine / Docker Desktop** (Dành cho việc kéo nhanh cơ sở dữ liệu MySQL và Elasticsearch).

---

## 🚀 Hướng Dẫn Cài Đặt và Khởi Chạy

### Bước 1: Khởi động hệ sinh thái Database bằng Docker

Bạn không cần tốn công cài đặt MySQL hay cấu hình Elasticsearch bằng tay. Chỉ cần nhập các lệnh sau trực tiếp trên Terminal để tạo container ảo:

**1. Pull & Chạy MySQL (Lưu trữ quan hệ chính):**
```bash
docker run -d --name brosport-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=brosport_db mysql:8.0
```
> *(Hệ thống spring root `application.properties` đang cấu hình port 3306, user `root`, pass `123456` và DB tên `brosport_db`)*

**2. Pull & Chạy Elasticsearch (Bộ máy tìm kiếm siêu tốc độ):**
```bash
docker run -d --name brosport-es -p 9200:9200 -e "discovery.type=single-node" -e "xpack.security.enabled=false" -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" elasticsearch:8.11.1
```
> *(Bắt buộc dùng biến môi trường `xpack.security.enabled=false` để vô hiệu hóa tầng Token, cho phép truy cập Localhost bằng HTTP trực tiếp vào cổng 9200 theo cấu hình Spring hiện tại).*

---

### Bước 2: Kiểm tra cấu hình kết nối ứng dụng

Truy cập file `src/main/resources/application.properties` và đảm bảo các cài đặt đã khớp:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/brosport_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=123456

spring.elasticsearch.uris=http://localhost:9200
```
> Hệ thống áp dụng cấu hình `spring.jpa.hibernate.ddl-auto=update` và sẽ tự động chạy **DataSeeder.java** để bơm Data (30-40 sản phẩm, review, lịch sử giá sale, admin, chat nội bộ) khi nó phát hiện DB đang trống ở lần khởi động đầu.

---

### Bước 3: Build và Chạy Spring Boot Web Application

Tại thư mục Root của bộ source code, mở Command Line:

```bash
# 1. Clean và build lại package 
mvn clean compile

# 2. Khởi chạy 
mvn spring-boot:run
```

Nếu bạn thao tác qua IDE (như IntelliJ / Eclipse), bạn chỉ việc bấm Run `DemoApplication.java`.

---

### Bước 4: Trải Nghiệm và Sử Dụng

Khoảng vài giây sau khi Spring Boot in ra Log thành công `Started DemoApplication`, bạn có thể truy cập qua trình duyệt:

- 🌐 **Giao diện mua hàng:** [http://localhost:8080/](http://localhost:8080/)
- ⚙️ **Dashboard Quản Trị / Chat Admin:** [http://localhost:8080/admin/chat](http://localhost:8080/admin/chat)
- 📝 **Tài liệu API (Swagger UI):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

#### Phân quyền Test đã nạp sẵn:
Lúc Data Seeder mồi dữ liệu, hệ thống tự động sinh tài khoản đã được Hash Password:

| Phân Quyền | Tên Đăng Nhập | Mật Khẩu |
| :--- | :--- | :--- |
| **Quản trị (Admin)** | `admin@brosport.com` | `12345` |
| **Khách mua hàng 1** | `customer1@brosport.com` | `12345` |
| **Khách mua hàng 2** | `customer2@brosport.com` | `12345` |

Chúc bạn có thời gian code và chạy test thành công! ^^
