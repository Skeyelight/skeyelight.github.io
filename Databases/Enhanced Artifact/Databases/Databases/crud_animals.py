# Author: Thomas Davis
#
# Project: Animal Collection Database
#
# Purpose: Provide a functional dashboard to interact with and query a database containing information on animals
#   available for adoption.
#
# More Information: This original project provided a dashboard with predefined queries to allow the ability to easily
#   find suitable animals for search and rescue. This enhancement has added the ability for custom queries, role-based
#   views, and the ability to add, remove, or update entries from the dashboard for administrators.


import os
from dotenv import load_dotenv
from pymongo import MongoClient

# CRUD operations for the AAC MongoDB
class AnimalShelter:

    # Connect to MongoDB using env file
    def __init__(self):
        load_dotenv()
        uri = os.getenv("MONGO_URI")
        # Throws an error if the .env file is not correctly configured
        if not uri:
            raise ValueError("No MONGO_URI found. Please check your .env file.")

        # Connects to correct database using the provided uri
        self.client = MongoClient(uri)
        self.database = self.client["AAC"]
        self.collection = self.database["animals"]
        self.create_indexes()

    # Create
    def create(self, data):
        if data:
            return self.collection.insert_one(data).acknowledged
        return False

    # Read
    def read(self, query):
        if query:
            return list(self.collection.find(query))
        return list(self.collection.find({}))

    # Update
    def update(self, query, data):
        if query:
            result = self.collection.update_many(query, {"$set": data})
            return result.modified_count
        return 0

    # Delete
    def delete(self, query):
        if query:
            result = self.collection.delete_many(query)
            return result.deleted_count
        return 0

    # Aggregate
    def aggregate(self, pipeline):
        return list(self.collection.aggregate(pipeline))

    # Indexes
    def create_indexes(self):
        self.collection.create_index("animal_type")
        self.collection.create_index("breed")
        self.collection.create_index([("animal_type", 1), ("breed", 1)])
