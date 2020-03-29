from flask import Flask, Response, render_template
from flask import request, jsonify, redirect
from flask import make_response
import os

_name_ = "AppAwesomeBackend FullBlown"

template_dir = os.path.abspath('static/template')
app     = Flask(_name_, template_folder=template_dir)

@app.after_request
def apply_otherHeaders(response):
  response.headers.set('Cache-Control', "no-cache, max-age=0, must-revalidate, no-store")        
  response.headers.set('X-XSS-Protection', "0")
  return response

@app.route("/home")
def index():      
  return app.send_static_file('parent.html')

@app.route("/child")
def child():         
  return app.send_static_file('child.html')


@app.route("/checkout", methods=['GET'])
def checkout():
  userInput = request.args.get('password')
  r = make_response(render_template('checkout.html', ui=userInput))
  return r 

if __name__ == '__main__':
  app.run(debug=True, host="192.168.1.34", port=31337)
