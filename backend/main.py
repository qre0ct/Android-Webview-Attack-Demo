from flask import Flask, Response, render_template
from flask import request, jsonify, redirect
from flask import make_response
import os

_name_ = "AppAwesome"
template_dir = os.path.abspath('static/template')
app     = Flask(_name_, template_folder=template_dir)

@app.route("/home")
def home():      
  return app.send_static_file('appAwesomeCheckout.html')


@app.route("/checkout")
def checkout():      
  return render_template('checkout.html')  
   

if __name__ == '__main__':
  app.run(debug=True, host="192.168.1.35", port=31337)