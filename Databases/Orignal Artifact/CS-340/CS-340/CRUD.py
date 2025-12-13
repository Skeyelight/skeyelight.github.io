from pymongo import MongoClient
from bson.objectid import ObjectId

class AnimalShelter(object):
    """ CRUD operations for Animal collection in MongoDB """

    def __init__(self, username, password):
        # Initializing the MongoClient. This helps to 
        # access the MongoDB databases and collections.
        # This is hard-wired to use the aac database, the 
        # animals collection, and the aac user.
        #
        # You must edit the connection variables below to reflect
        # your own instance of MongoDB!
        #
        # Connection Variables
        #
        HOST = 'nv-desktop-services.apporto.com'
        PORT = 31345
        DB = 'AAC'
        COL = 'animals'
        
        # Initialize Connection
        # Using an f-string for a more modern and readable connection string
        self.client = MongoClient(f'mongodb://{username}:{password}@{HOST}:{PORT}')
        self.database = self.client[DB]
        self.collection = self.database[COL]

    # Method to implement the C in CRUD.
    def create(self, data):
        # Inserts a document
        if data is not None:
            insert_doc = self.collection.insert_one(data)
            return insert_doc.acknowledged
        else:
            raise False

    # Method to implement the R in CRUD.
    def read(self, query):
        # Queries for document
        if query is not None:
            read_doc = self.collection.find(query)
            return list(read_doc)

    # Method to implement the U in CRUD.
    def update(self, query, data):
        # Updates document
        if query is not None:
            update_result = self.collection.update_many(query, {"$set": data})
            return update_result.modified_count
    
    # Method to implement the D in CRUD.   
    def delete(self, query):
        # Deletes documents
        delete_result = self.collection.delete_many(query)
        return delete_result.deleted_count
