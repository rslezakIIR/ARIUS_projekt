from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_jwt_extended import JWTManager
from flask_mail import Mail
from flask_cors import CORS
from config import config

# Initialize extensions
db = SQLAlchemy()
migrate = Migrate()
jwt = JWTManager()
mail = Mail()

def create_app(config_name='default'):
    app = Flask(__name__)
    
    # Load configuration from config object
    app.config.from_object(config[config_name])
    
    # Initialize extensions with the Flask app
    db.init_app(app)
    migrate.init_app(app, db)
    jwt.init_app(app)
    mail.init_app(app)
    
    # Enable CORS for API routes only
    CORS(app, resources={r"/api/*": {"origins": "http://localhost:5173"}})
    
    # Register blueprints
    from app.routes.auth import auth
    from app.routes.coins import coins
    from app.routes.orders import orders
    from app.routes.reviews import reviews
    
    app.register_blueprint(auth, url_prefix='/api/auth')
    app.register_blueprint(coins, url_prefix='/api/coins')
    app.register_blueprint(orders, url_prefix='/api/orders')
    app.register_blueprint(reviews, url_prefix='/api/reviews')
    
    # Health check route
    @app.route('/health')
    def health_check():
        return {'status': 'healthy'}, 200
    
    # Ensure database tables are created (development only)
    with app.app_context():
        db.create_all()
    
    return app
