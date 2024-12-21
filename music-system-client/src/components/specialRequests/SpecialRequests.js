import React, { useState } from "react";
import {
    countLabelsAboveThreshold,
    findDescriptionsByPrefix,
    addSingleToBand,
    addParticipantToBand,
} from "../../services/api";
import styles from "./SpecialRequests.module.css";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const SpecialRequests = () => {
    // Состояния для существующих операций
    const [countLabelsThreshold, setCountLabelsThreshold] = useState("");
    const [countLabelsResult, setCountLabelsResult] = useState(null);
    const [descriptionPrefix, setDescriptionPrefix] = useState("");
    const [descriptionResult, setDescriptionResult] = useState([]);

    // Состояния для новых операций
    const [singleBandId, setSingleBandId] = useState("");
    const [singlesToAdd, setSinglesToAdd] = useState("");
    const [participantBandId, setParticipantBandId] = useState("");
    const [participantsToAdd, setParticipantsToAdd] = useState("");

    // Общие состояния
    const [loading, setLoading] = useState(false);

    const handleCountLabels = async () => {
        setLoading(true);
        try {
            const result = await countLabelsAboveThreshold(countLabelsThreshold);
            setCountLabelsResult(result);
            toast.success("Labels counted successfully!");
        } catch (err) {
            toast.error(`Failed to count labels: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleFindDescriptions = async () => {
        setLoading(true);
        try {
            const result = await findDescriptionsByPrefix(descriptionPrefix);
            setDescriptionResult(result);
            toast.success("Descriptions fetched successfully!");
        } catch (err) {
            toast.error(`Failed to fetch descriptions: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleAddSingle = async () => {
        setLoading(true);
        try {
            await addSingleToBand(singleBandId, singlesToAdd);
            toast.success("Single added successfully!");
        } catch (err) {
            toast.error(`Failed to add single: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleAddParticipant = async () => {
        setLoading(true);
        try {
            await addParticipantToBand(participantBandId, participantsToAdd);
            toast.success("Participant added successfully!");
        } catch (err) {
            toast.error(`Failed to add participant: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className={styles.specialRequests}>
            <ToastContainer />
            <h2>Special Requests</h2>

            {/* Подсчет лейблов */}
            <div className={styles.requestSection}>
                <h3>Count Labels Above Threshold</h3>
                <input
                    type="number"
                    value={countLabelsThreshold}
                    onChange={(e) => setCountLabelsThreshold(e.target.value)}
                    placeholder="Enter threshold"
                />
                <button onClick={handleCountLabels} disabled={loading || !countLabelsThreshold}>
                    Fetch Data
                </button>
                {countLabelsResult !== null && <p>Result: {countLabelsResult}</p>}
            </div>

            {/* Поиск описаний по префиксу */}
            <div className={styles.requestSection}>
                <h3>Find Descriptions by Prefix</h3>
                <input
                    type="text"
                    value={descriptionPrefix}
                    onChange={(e) => setDescriptionPrefix(e.target.value)}
                    placeholder="Enter prefix"
                />
                <button onClick={handleFindDescriptions} disabled={loading || !descriptionPrefix}>
                    Fetch Data
                </button>
                {descriptionResult.length > 0 && (
                    <ul>
                        {descriptionResult.map((item, index) => (
                            <li key={item.id}>
                                ID: {item.id}, Description: {item.description}
                            </li>
                        ))}
                    </ul>
                )}
            </div>

            {/* Добавление сингла */}
            <div className={styles.requestSection}>
                <h3>Add Single to Band</h3>
                <input
                    type="number"
                    value={singleBandId}
                    onChange={(e) => setSingleBandId(e.target.value)}
                    placeholder="Enter Band ID"
                />
                <input
                    type="number"
                    value={singlesToAdd}
                    onChange={(e) => setSinglesToAdd(e.target.value)}
                    placeholder="Enter Singles Count"
                />
                <button onClick={handleAddSingle} disabled={loading || !singleBandId || !singlesToAdd}>
                    Add Single
                </button>
            </div>

            {/* Добавление участника */}
            <div className={styles.requestSection}>
                <h3>Add Participant to Band</h3>
                <input
                    type="number"
                    value={participantBandId}
                    onChange={(e) => setParticipantBandId(e.target.value)}
                    placeholder="Enter Band ID"
                />
                <input
                    type="number"
                    value={participantsToAdd}
                    onChange={(e) => setParticipantsToAdd(e.target.value)}
                    placeholder="Enter Participants Count"
                />
                <button onClick={handleAddParticipant} disabled={loading || !participantBandId || !participantsToAdd}>
                    Add Participant
                </button>
            </div>
        </div>
    );
};

export default SpecialRequests;
