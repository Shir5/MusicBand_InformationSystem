import React from "react";
import PropTypes from "prop-types";
import styles from "./FilterInput.module.css";

const FilterInput = ({ filterQuery, setFilterQuery, filterColumn, setFilterColumn }) => {
    return (
        <div className={styles.filterContainer}>
            <label htmlFor="filterColumn">Filter By:</label>
            <select
                id="filterColumn"
                value={filterColumn}
                onChange={(e) => setFilterColumn(e.target.value)}
                className={styles.dropdown}
            >
                <option value="name">Name</option>
                <option value="description">Description</option>
                <option value="genre">Genre</option>
            </select>
            <label htmlFor="filterQuery">Filter Value:</label>
            <input
                id="filterQuery"
                type="text"
                value={filterQuery}
                onChange={(e) => setFilterQuery(e.target.value)}
                placeholder={`Search by ${filterColumn}...`}
                className={styles.input}
            />
        </div>
    );
};

FilterInput.propTypes = {
    filterQuery: PropTypes.string.isRequired,
    setFilterQuery: PropTypes.func.isRequired,
    filterColumn: PropTypes.string.isRequired,
    setFilterColumn: PropTypes.func.isRequired,
};

export default FilterInput;
