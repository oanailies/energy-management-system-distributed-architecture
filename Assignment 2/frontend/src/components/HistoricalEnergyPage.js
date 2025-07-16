import React, { useState } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, BarChart, Bar } from 'recharts';
import '../styles/historical-energy.css';

const HistoricalEnergyPage = () => {
    const [deviceId, setDeviceId] = useState('');
    const [selectedDate, setSelectedDate] = useState('');
    const [chartData, setChartData] = useState([]);
    const [error, setError] = useState('');
    const [chartType, setChartType] = useState('line');

    const fetchChartData = async () => {
        if (!deviceId || !selectedDate) {
            setError('Please select both a device ID and a date.');
            return;
        }

        try {
            const response = await axios.get(
                `http://receiver.localhost/api/consumptions/device/${deviceId}/date/aggregate`,
                { params: { date: selectedDate } }
            );

            setChartData(
                response.data.map((item) => ({
                    hour: item.hour,
                    totalConsumption: parseFloat(item.totalConsumption.toFixed(2)),
                }))
            );

            setError('');
        } catch (error) {
            setError('Failed to load chart data. Please try again.');
        }
    };

    const renderChart = () => {
        if (chartData.length === 0) {
            return <p>No data available for the selected date.</p>;
        }

        if (chartType === 'line') {
            return (
                <LineChart width={800} height={400} data={chartData}>
                    <CartesianGrid stroke="#ccc" />
                    <XAxis dataKey="hour" label={{ value: "Hours", position: "insideBottomRight", offset: -10 }} />
                    <YAxis label={{ value: "Consumption (kWh)", angle: -90, position: "insideLeft" }} />
                    <Tooltip />
                    <Line type="monotone" dataKey="totalConsumption" stroke="#8884d8" />
                </LineChart>
            );
        } else {
            return (
                <BarChart width={800} height={400} data={chartData}>
                    <CartesianGrid stroke="#ccc" />
                    <XAxis dataKey="hour" label={{ value: "Hours", position: "insideBottomRight", offset: -10 }} />
                    <YAxis label={{ value: "Consumption (kWh)", angle: -90, position: "insideLeft" }} />
                    <Tooltip />
                    <Bar dataKey="totalConsumption" fill="#8884d8" />
                </BarChart>
            );
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1>Historical Energy Consumption</h1>
            <div style={{ marginBottom: '20px' }}>
                <label>
                    Device ID:
                    <input
                        type="number"
                        value={deviceId}
                        onChange={(e) => setDeviceId(e.target.value)}
                        placeholder="Enter Device ID"
                        style={{ margin: '0 10px' }}
                    />
                </label>
                <label>
                    Date:
                    <input
                        type="date"
                        value={selectedDate}
                        onChange={(e) => setSelectedDate(e.target.value)}
                        style={{ margin: '0 10px' }}
                    />
                </label>
                <button onClick={fetchChartData} style={{ margin: '0 10px' }}>
                    Fetch Data
                </button>
            </div>
            <div style={{ marginBottom: '20px' }}>
                <button onClick={() => setChartType('line')} style={{ marginRight: '10px' }}>
                    Line Chart
                </button>
                <button onClick={() => setChartType('bar')}>Bar Chart</button>
            </div>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {renderChart()}
        </div>
    );
};

export default HistoricalEnergyPage;
