import React, { useState, useEffect } from 'react';
import axios from 'axios';
import PersonForm from './components/PersonForm';
import PersonList from './components/PersonList';
import './components/styles.css';
function App() {
    const [people, setPeople] = useState([]);

    useEffect(() => {
        fetchPeople();
    }, []);

    async function fetchPeople() {
        try {
            const response = await axios.get('http://localhost:8080/api/people');
            setPeople(response.data);
        } catch (error) {
            console.error('Error fetching people:', error);
        }
    }
    return (
        <div>
            <h1>Person Management System</h1>
            <PersonForm fetchPeople={fetchPeople} />
            <PersonList people={people} />
        </div>
    );
}

export default App;
