import bcrypt
import os
from dotenv import load_dotenv
from pymongo import MongoClient

# Handles user authentication
class UserAuth:
    # Connect to MongoDB using env file
    def __init__(self):
        load_dotenv()
        uri = os.getenv("MONGO_URI")

        if not uri:
            raise ValueError("No MONGO_URI found. Please check your .env file.")

        self.client = MongoClient(uri)
        self.database = self.client["AAC"]
        self.users = self.database["users"]

    # Create new user
    def create_user(self, username, password):

        # Prevent duplicate usernames
        if self.users.find_one({"username": username}):
            return False, "Username already exists."

        # Hash password
        hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt())

        # Insert User, default role of user
        self.users.insert_one({
            "username": username,
            "password": hashed,
            "role": "user"
        })
        return True, "Account created successfully."

    # Validate login
    def validate_user(self, username, password):
        user = self.users.find_one({"username": username})
        if not user:
            return False

        stored_hash = user["password"]
        return bcrypt.checkpw(password.encode(), stored_hash)

    def get_role(self, username):
        # Fetches the role for a specific username
        user = self.users.find_one({"username": username})
        if user:
            return user.get("role", "user")
        return "user"