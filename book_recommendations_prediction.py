import firebase_admin
from firebase_admin import credentials, db
import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Input, Embedding, Flatten, Concatenate, Dense, Dropout

# Firebase setup
def initialize_firebase():
    if not firebase_admin._apps:  # Check if Firebase has been initialized
        cred = credentials.Certificate("C:/Users/User/.spyder-py3/librarymanagementsystemai-firebase-adminsdk-g9e11-e0e6b1804b.json")
        firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://librarymanagementsystemai-default-rtdb.asia-southeast1.firebasedatabase.app/'
        })

# Initialize Firebase
initialize_firebase()

# Fetch data from Firebase Realtime Database
ref = db.reference('UserBorrowingHistoryPermanent')
data = ref.get()

# Function to train model for a specific user
def train_model_for_user(user_id, user_df):
    print(f"Training model for User ID {user_id} with {len(user_df)} records")
    
    # Print the author and genre values before encoding
    print("Before encoding:")
    print(user_df[['book_author', 'book_genre']])

    # Preprocessing: Encode book_author and book_genre as integers
    user_df['book_author_encoded'] = user_df['book_author'].astype('category').cat.codes
    user_df['book_genre_encoded'] = user_df['book_genre'].astype('category').cat.codes

    # Print the author and genre values after encoding
    print("After encoding:")
    print(user_df[['book_author_encoded', 'book_genre_encoded']])

    # Create mappings to decode the encoded values later
    author_decoder = dict(enumerate(user_df['book_author'].astype('category').cat.categories))
    genre_decoder = dict(enumerate(user_df['book_genre'].astype('category').cat.categories))

    num_authors = user_df['book_author_encoded'].nunique()
    num_genres = user_df['book_genre_encoded'].nunique()

    print(f"Unique authors: {num_authors}, Unique genres: {num_genres}")

    # Input layers for author and genre
    author_input = Input(shape=(1,), name='author_input')
    genre_input = Input(shape=(1,), name='genre_input')

    # Embedding layers for author and genre
    author_embedding = Embedding(num_authors + 1, 10, name='author_embedding')(author_input)
    genre_embedding = Embedding(num_genres + 1, 10, name='genre_embedding')(genre_input)

    # Flatten the embedding layers
    author_flat = Flatten()(author_embedding)
    genre_flat = Flatten()(genre_embedding)

    # Concatenate author and genre embeddings
    concatenated = Concatenate()([author_flat, genre_flat])

    # Add dense layers for prediction
    dense_1 = Dense(128, activation='relu')(concatenated)
    dense_1 = Dropout(0.5)(dense_1)
    dense_2 = Dense(64, activation='relu')(dense_1)
    output = Dense(1, activation='sigmoid')(dense_2)

    # Define the model
    model = Model([author_input, genre_input], output)
    model.compile(optimizer='adam', loss='binary_crossentropy')

    # Train the model on the specific user's data
    X = [user_df['book_author_encoded'].values, user_df['book_genre_encoded'].values]
    y = np.random.rand(len(user_df))  # You would use actual user interaction data here

    model.fit(X, y, epochs=5, batch_size=16)

    # Return the model and the decoders to map back encoded values to original strings
    return model, author_decoder, genre_decoder

# Function to make recommendations for a user and store them in Firebase
def recommend_books_for_user(user_id):
    # Fetch user's borrow history
    user_books = data.get(user_id, {})
    
    # Convert user data into a DataFrame
    user_rows = []
    for book_id, borrows in user_books.items():
        for borrow_id, borrow_info in borrows.items():
            user_rows.append({
                'book_id': book_id,
                'borrow_id': borrow_id,
                'book_author': borrow_info.get('bookAuthor', ''),
                'book_genre': borrow_info.get('bookGenre', '')
            })

    user_df = pd.DataFrame(user_rows)
    
    if user_df.empty:
        print(f"No borrowing history found for user {user_id}.")
        return

    # Train a model for the user
    model, author_decoder, genre_decoder = train_model_for_user(user_id, user_df)

    # Make recommendations by predicting the score for each book in the user's history
    for idx, row in user_df.iterrows():
        author_encoded = row['book_author_encoded']
        genre_encoded = row['book_genre_encoded']

        # Convert inputs to numpy arrays for prediction
        author_array = np.array([[author_encoded]])
        genre_array = np.array([[genre_encoded]])

        # Make prediction
        prediction = model.predict([author_array, genre_array])

        # Decode the predicted author and genre using the decoder mappings
        decoded_author = author_decoder.get(author_encoded, 'Unknown')
        decoded_genre = genre_decoder.get(genre_encoded, 'Unknown')

        print(f"Top recommendation for User ID {user_id}:")
        print(f"Author: {decoded_author}, Genre: {decoded_genre}, Predicted Score: {prediction[0][0]}\n")

        # Store the decoded author and genre along with the predicted score in Firebase
        user_recommendations_ref = db.reference(f'Recommendations/{user_id}')
        user_recommendations_ref.set({
            'recommended_author': decoded_author,
            'recommended_genre': decoded_genre,
            'predicted_score': float(prediction[0][0])
        })

        print(f"Recommendations for User {user_id} stored in Firebase.\n")

# Iterate over each user in the dataset and generate recommendations
for user_id in data.keys():
    recommend_books_for_user(user_id)
