import os
from app import create_app, db
from app.models.user import User

# Tworzenie aplikacji i kontekstu
app = create_app()
app.app_context().push()

# Usunięcie istniejących danych
print("Usuwam istniejących użytkowników...")
User.query.delete()
db.session.commit()

# Tworzenie nowych użytkowników
print("Tworzę nowych użytkowników...")
users = [
    User(email="john.doe@example.com", username="JohnDoe"),
    User(email="jane.doe@example.com", username="JaneDoe"),
    User(email="admin@example.com", username="Admin"),
]

# Ustawianie haseł
users[0].set_password("password123")  # Hasło dla JohnDoe
users[1].set_password("securepassword")  # Hasło dla JaneDoe
users[2].set_password("adminpassword")  # Hasło dla Admin

# Dodanie użytkowników do sesji
for user in users:
    db.session.add(user)

# Zapis do bazy danych
try:
    db.session.commit()
    print("Użytkownicy zostali dodani do bazy danych!")
except Exception as e:
    db.session.rollback()
    print("Wystąpił błąd podczas zapisu do bazy:", str(e))

# Wylistowanie użytkowników w bazie (debug)
print("Użytkownicy w bazie danych:")
for user in User.query.all():
    print(f"ID: {user.id}, Email: {user.email}, Username: {user.username}, Created At: {user.created_at}")
