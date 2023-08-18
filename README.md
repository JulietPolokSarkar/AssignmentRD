# AssignmentRD
AssignmentRD is a web application built using Spring Boot, Java, MySQL, and React that offers a comprehensive solution for verifying identity documents and conducting image comparisons. It leverages Microsoft Azure's Form Recognizer service to validate entered data against uploaded documents, highlighting any discrepancies. Additionally, it utilizes Azure's Face API to assess the probability of correspondence between uploaded ID cards/passports and portrait photos. Experience a streamlined, secure, and accurate verification process with this web app.

# Features
Data Validation: The application leverages Microsoft Azure's Form Recognizer service to validate the correctness of entered data against uploaded identity documents. It meticulously cross-checks information such as name, date of birth, place of birth, nationality, and more.

Image Comparison: It employs Microsoft Azure's Face API to assess the probability that uploaded ID cards/passports and portrait photos correspond to the same individual. Intelligent image preprocessing techniques enhance the accuracy of the comparison.

# Technologies Used
Spring Boot: Backend framework for building robust and efficient Java applications.
Java: The programming language used for developing both the backend and frontend logic.
MySQL: The database management system for storing and managing user and application data.
React: A powerful JavaScript library for building dynamic and interactive user interfaces.
Microsoft Azure Form Recognizer: A cloud-based service for extracting structured data from documents.
Microsoft Azure Face API: A service that detects and analyzes faces in images, enabling facial recognition and verification.

# Getting Started
1. Clone the repository
2. Backend Setup:
Create a MySQL database and update the database configuration in the application.properties file.
Run the Spring Boot application using your preferred IDE
3. Frontend Setup:
Navigate to the frontend directory
Install dependencies and start the React app
4. Replace the Azure form recognizer and face API keys and endpoints with your own.
5. Access the Application:
Open your browser and navigate to http://localhost:3000 to use the App.




# Usage
Upload Documents: Upload identity documents such as ID cards or passports using the provided form.

Enter Data: Enter personal information in the corresponding fields for validation.

Retrieve Data: Retrieve personal information.

Validation Result: Receive instant validation results highlighting any discrepancies between entered data and extracted data from the uploaded document.

Image Comparison: Upload portrait photos and compare them with the uploaded ID documents to assess the probability of a match.

# Acknowledgments
Microsoft Azure: Providing robust services for identity validation and image comparison.
Spring Boot and React communities: Creating powerful frameworks and libraries that enable seamless application development.
Feel free to contribute, report issues, and provide feedback to help enhance this web app.
