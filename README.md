1. This is the backend that  returns CSP headers. It does not have any inline javascript. But it is deliberately vulnerable to reflected XSS, thus allowing injection of javascript. It also returns the X-XSS-Protection: 0 header
2. To run the backend, please update the IP, in the main.py,  with your own IP
