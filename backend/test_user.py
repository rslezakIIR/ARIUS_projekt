from app import create_app, db
from app.models.user import User

# Tworzenie instancji aplikacji Flask
app = create_app()

# Użycie kontekstu aplikacji
with app.app_context():
    user = User.query.filter_by(email="john.doe@example.com").first()
    if user:
        print("Email:", user.email)
        print("Password Hash:", user.password_hash)
        print("Check Password:", user.check_password("password123"))  # Powinno zwrócić True
    else:
        print("Użytkownik nie istnieje.")
