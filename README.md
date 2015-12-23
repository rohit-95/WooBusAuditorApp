# WooBusAuditorApp

Dependencies:
Volley: https://android.googlesource.com/platform/frameworks/volley

After importing volley as a module, if using API 23 or above add
  useLibrary  "org.apache.http.legacy"
to 
  android {
  }
section of build.gradle of volley not the app.
