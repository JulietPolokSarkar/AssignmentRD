import { FormRecognizerClient, AzureKeyCredential } from '@azure/ai-form-recognizer';
import React, { useState } from 'react';
import axios from 'axios';
import './styles.css';

function PersonForm({ fetchPeople }) {
    const [person, setPerson] = useState({
        firstName: '',
        lastName: '',
        dateOfBirth: '',
        placeOfBirth: '',
        nationality: '',
        gender: '',
        address: '',
        email: '',
        phoneNumber: '',
        portraitPhoto: null,
        idDocument: null,
    });

    const [validationErrors, setValidationErrors] = useState({
        firstName: '',
        lastName: '',
        dateOfBirth: '',
        placeOfBirth: '',
        nationality: '',
        gender: '',
        address: '',
        email: '',
        phoneNumber: '',
        portraitPhoto: '',
        idDocument: '',
    });

    const [comparisonResult, setComparisonResult] = useState(null);
    // const subscriptionKey = 'your_form_recognizer_subscription_key';
    // const endpoint = 'your_form_recognizer_endpoint';
    //
    // const formRecognizerClient = new FormRecognizerClient(
    //     endpoint,
    //     new AzureKeyCredential(subscriptionKey)
    // );

    const handleChange = (e) => {
        const { name, value } = e.target;
        setPerson((prevPerson) => ({ ...prevPerson, [name]: value }));
    };

    const handleImageChange = (e) => {
        const { name, files } = e.target;
        setPerson((prevPerson) => ({ ...prevPerson, [name]: files[0] }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Frontend data validation logic
        const validationErrors = {};

        // Example: Check if first name is entered
        if (!person.firstName) {
            validationErrors.firstName = 'First Name is required.';
        }
        // Example: Check if last name is entered
        if (!person.lastName) {
            validationErrors.lastName = 'Last Name is required.';
        }

        // Example: Check if date of birth is entered
        if (!person.dateOfBirth) {
            validationErrors.dateOfBirth = 'Date of Birth is required.';
        }

        // Example: Check if place of birth is entered
        if (!person.placeOfBirth) {
            validationErrors.placeOfBirth = 'Place of Birth is required.';
        }

        // Example: Check if nationality is entered
        if (!person.nationality) {
            validationErrors.nationality = 'Nationality is required.';
        }

        // Example: Check if gender is selected
        if (!person.gender) {
            validationErrors.gender = 'Gender is required.';
        }

        // Example: Check if address is entered
        if (!person.address) {
            validationErrors.address = 'Address is required.';
        }

        // Example: Check if email is entered and in valid format
        if (!person.email) {
            validationErrors.email = 'Email is required.';
        } else if (!/\S+@\S+\.\S+/.test(person.email)) {
            validationErrors.email = 'Email is not in valid format.';
        }

        // Example: Check if phone number is entered and in valid format
        if (!person.phoneNumber) {
            validationErrors.phoneNumber = 'Phone Number is required.';
        } else if (!/^\d{10}$/.test(person.phoneNumber)) {
            validationErrors.phoneNumber = 'Phone Number should be 10 digits.';
        }

        // Example: Check if portrait photo is uploaded
        if (!person.portraitPhoto) {
            validationErrors.portraitPhoto = 'Portrait Photo is required.';
        }

        // Example: Check if ID document is uploaded
        if (!person.idDocument) {
            validationErrors.idDocument = 'ID Document is required.';
        }


        if (Object.keys(validationErrors).length > 0) {
            setValidationErrors(validationErrors);
            return; // Abort submission if there are validation errors
        }

        // Continue with form submission to backend
        const formData = new FormData();
        // Append form fields
        formData.append('firstName', person.firstName);
        formData.append('lastName', person.lastName);
        formData.append('dateOfBirth', person.dateOfBirth);
        formData.append('placeOfBirth', person.placeOfBirth);
        formData.append('nationality', person.nationality);
        formData.append('gender', person.gender);
        formData.append('address', person.address);
        formData.append('email', person.email);
        formData.append('phoneNumber', person.phoneNumber);

        // Append image files
        formData.append('portraitPhoto', person.portraitPhoto);
        formData.append('idDocument', person.idDocument);

        // // Validate uploaded document using Azure Form Recognizer
        // const validationMessage = await validateDocument(person.idDocument, person);
        // console.log('Validation Result:', validationMessage);
        //
        // // Update validation status based on validation and comparison results
        // if (response.data.includes('Validation Successful')) {
        //     setValidationStatus('validation-success');
        // } else {
        //     setValidationStatus('validation-failed');
        // }

        try {
            // Send the form data to your backend API for validation
            const validationResultMessage = await axios.post('http://localhost:8080/api/validation/validate', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            // Display validation result to the user
            alert(validationResultMessage.data);

            if (validationResultMessage.data.includes("Validation Successful")) {
                // If validation is successful, proceed with image comparison
                try {
                    const comparisonResponse = await axios.post('http://localhost:8080/api/image-comparison/compare', formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                        },
                    });

                    const comparisonResult = comparisonResponse.data;
                    alert('Image Comparison Result: ' + comparisonResult.confidence.toFixed(2));

                    if (comparisonResult.confidence > 0.5) {
                        // If confidence is above the threshold, proceed to submit the form
                        const response = await axios.post('http://localhost:8080/api/people', formData, {
                            headers: {
                                'Content-Type': 'multipart/form-data',
                            },
                        });
                        console.log('Person created:', response.data);
                        // Fetch the updated list of people after successful submission
                        fetchPeople();
                        // Reset the form after successful submission
                        setPerson({
                            firstName: '',
                            lastName: '',
                            dateOfBirth: '',
                            placeOfBirth: '',
                            nationality: '',
                            gender: '',
                            address: '',
                            email: '',
                            phoneNumber: '',
                            portraitPhoto: null,
                            idDocument: null,
                        });
                    }
                } catch (comparisonError) {
                    console.error('Error comparing images:', comparisonError);
                }
            }
        } catch (error) {
            console.error('Error creating person:', error);
        }
    };

        return (
        <div className="form-container">
            <h2>Add New Person</h2>
            <form onSubmit={handleSubmit}>
                {/* First Name */}
                <div className="form-group">
                    <label>First Name:</label>
                    <input type="text" name="firstName" value={person.firstName} onChange={handleChange} required />
                    {validationErrors.firstName && <div className="error-message">{validationErrors.firstName}</div>}
                </div>
                {/* Last Name */}
                <div className="form-group">
                    <label>Last Name:</label>
                    <input type="text" name="lastName" value={person.lastName} onChange={handleChange} required />
                    {validationErrors.lastName && <div className="error-message">{validationErrors.lastName}</div>}
                </div>
                {/* Date of Birth */}
                <div className="form-group">
                    <label>Date of Birth:</label>
                    <input type="date" name="dateOfBirth" value={person.dateOfBirth} onChange={handleChange} required />
                    {validationErrors.dateOfBirth && <div className="error-message">{validationErrors.dateOfBirth}</div>}
                </div>
                {/* Place of Birth */}
                <div className="form-group">
                    <label>Place of Birth:</label>
                    <input type="text" name="placeOfBirth" value={person.placeOfBirth} onChange={handleChange} required />
                    {validationErrors.placeOfBirth && <div className="error-message">{validationErrors.placeOfBirth}</div>}
                </div>
                {/* Nationality */}
                <div className="form-group">
                    <label>Nationality:</label>
                    <input type="text" name="nationality" value={person.nationality} onChange={handleChange} required />
                    {validationErrors.nationality && <div className="error-message">{validationErrors.nationality}</div>}
                </div>
                {/* Gender */}
                <div className="form-group">
                    <label>Gender:</label>
                    <select name="gender" value={person.gender} onChange={handleChange} required>
                        <option value="male">Male</option>
                        <option value="female">Female</option>
                        <option value="other">Other</option>
                    </select>
                    {validationErrors.gender && <div className="error-message">{validationErrors.gender}</div>}
                </div>
                {/* Address */}
                <div className="form-group">
                    <label>Address:</label>
                    <textarea name="address" value={person.address} onChange={handleChange} required />
                    {validationErrors.address && <div className="error-message">{validationErrors.address}</div>}
                </div>
                {/* Email */}
                <div className="form-group">
                    <label>Email:</label>
                    <input type="email" name="email" value={person.email} onChange={handleChange} required />
                    {validationErrors.email && <div className="error-message">{validationErrors.email}</div>}
                </div>
                {/* Phone Number */}
                <div className="form-group">
                    <label>Phone Number:</label>
                    <input type="tel" name="phoneNumber" value={person.phoneNumber} onChange={handleChange} required />
                    {validationErrors.phoneNumber && <div className="error-message">{validationErrors.phoneNumber}</div>}
                </div>
                {/* Portrait Photo */}
                <div className="form-group">
                    <label>Portrait Photo:</label>
                    <input
                        type="file"
                        name="portraitPhoto"
                        accept="image/*"
                        onChange={handleImageChange}
                        required
                    />
                    {validationErrors.portraitPhoto && <div className="error-message">{validationErrors.portraitPhoto}</div>}
                </div>
                {/* ID Document */}
                <div className="form-group">
                    <label>ID Document:</label>
                    <input
                        type="file"
                        name="idDocument"
                        accept="image/*"
                        onChange={handleImageChange}
                        required
                    />
                    {validationErrors.idDocument && <div className="error-message">{validationErrors.idDocument}</div>}
                </div>
                {/* Display validation errors */}
                {Object.keys(validationErrors).length > 0 && (
                    <div className="validation-errors">
                        {Object.values(validationErrors).map((error, index) => (
                            <p key={index}>{error}</p>
                        ))}
                    </div>
                )}
                {/* Submit Button */}
                <div className="form-group">
                    <button className="submit-button" type="submit">Add Person</button>
                    {comparisonResult !== null && (
                        <div className="comparison-result">
                            Image Comparison Result: {comparisonResult.confidence.toFixed(2)}
                        </div>
                    )}
                </div>
            </form>
        </div>
    );
}

export default PersonForm;
