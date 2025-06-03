# DBGrab

**DBGrab** is a Java-based utility designed to connect to relational databases, execute queries, and extract data in a structured and configurable format. Ideal for developers, testers, and data engineers who need a lightweight CLI tool to interact with databases quickly.

## ✨ Features

- Connect to popular relational databases (MySQL, PostgreSQL, Oracle, etc.)
- Run SQL queries and export the result
- Customizable database and query configuration
- Logging and output file generation

## 🛠️ Technologies Used

- Java
- Apache Maven
- JDBC
- Batch scripting (`run.cmd`)
- Log4j (if logging framework is used)

## 📁 Project Structure

```
DBGrab/
├── config/          # Configuration files (e.g., DB connection settings)
├── logs/            # Generated logs
├── output/          # Output results from executed queries
├── src/main/        # Java source code
├── run.cmd          # Windows script to run the application
├── pom.xml          # Maven build file
└── .gitignore
```

## 🚀 Getting Started

### Prerequisites

- Java 8 or higher
- Maven installed and configured

### Clone the Repository

```bash
git clone https://github.com/mdey0108/DBGrab.git
cd DBGrab
```

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
./run.cmd
```

> Note: Ensure you have updated `config/db.properties` (or equivalent config file) with the correct DB credentials and connection details.

## ⚙️ Configuration

Update your database connection and query details inside the `config/` folder.

Example (`config/db.properties`):

```properties
db.url=jdbc:mysql://localhost:3306/mydb
db.username=root
db.password=yourpassword
db.query=SELECT * FROM users;
```

## 📦 Output

- Results are saved in the `output/` directory.
- Logs are stored in the `logs/` folder.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙌 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
