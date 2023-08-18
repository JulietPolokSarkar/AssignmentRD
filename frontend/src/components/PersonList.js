import React from 'react';
import './styles.css';

function PersonList({ people }) {
    return (
        <div className="container">
            <h2>People List</h2>
            <table className="person-list">
                <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Date of Birth</th>
                    <th>Place of Birth</th>
                    <th>Nationality</th>
                    <th>Gender</th>
                    <th>Address</th>
                    <th>Email</th>
                    <th>Phone Number</th>
                    <th>Portrait Photo</th>
                    <th>ID Document</th>
                    <th>Validation Status</th>
                </tr>
                </thead>
                <tbody>
                {people.map((person) => (
                    <tr key={person.id}>
                        <td>{person.firstName}</td>
                        <td>{person.lastName}</td>
                        <td>{person.dateOfBirth}</td>
                        <td>{person.placeOfBirth}</td>
                        <td>{person.nationality}</td>
                        <td>{person.gender}</td>
                        <td>{person.address}</td>
                        <td>{person.email}</td>
                        <td>{person.phoneNumber}</td>
                        <td><img src={person.portraitPhoto} alt="Portrait" /></td>
                        <td><img src={person.idDocument} alt="ID Document" /></td>
                        <td>
                            {person.validationResultMessage === 'Success'
                                ? <span className="validation-success">Validation Successful</span>
                                : <span className="validation-failed">Validation Failed</span>
                            }
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}
export default PersonList;
