# Project CRC Cards

### User
|Responsibilities | Collaborators |
|:---             | :---          |
| Sign into app | LoginManager |
| Hold user permissions (Entrant, Organizer, Admin)| |
| Provide device information | LoginManager |
| Store entrant personal information| Entrant|
| Update personal information | Entrant |
| Store user image | ImageManager |

### Entrant
|Responsibilities | Collaborators |
|:---             | :---          |
|View the state of events |ListManager |
|Join/leave a waiting list of event |ListManager |
|Provide/update personal information: name, email, optional phone number | User |
|Upload/remove profile picture | User |
|Opt in/out of notifications from organizers and admin | NotificationManager |
|Recieve notifications from organizers | NotificationManager |
|Accept/decline event invitations | List Manager |
|Scan QR code to register/navigate to event | QRManager|

### Organizer
|Responsibilities | Collaborators |
|:---             | :---          |
| Create event | Event |
| Edit event | |
| Create QR code | QRManager |
| Upload event image | ImageManager|
| View event waiting list| ListManager |
| Trigger system to sample event Entrants | ListManager |
| Optionally limit the number of entrants | ListManager |
| View invited list | ListManager|
| View accepted list | ListManager|
| View cancelled list | ListManager|
| Cancel entrants that declined and trigger a replacement |ListManager|
| Send notifications entrants (on waiting list, accepted, cancelled) | NotificationManager |
| View map showing where entrants joined | Map |

### Admin
|Responsibilities | Collaborators |
|:---             | :---          |
| Remove events | Event |
| Browse events|Event |
| Remove profiles | User |
| Browse profiles |User |
| Remove images | ImageManager |
| Browse images |ImageManager |
| Remove hashed QR code data | QRManager |
| Remove facilities | Organizer |

### LoginManager
|Responsibilities | Collaborators |
|:---             | :---          |
| Identify user based on device | User |
| Access database to manage users (add/remove) | |

### Event
|Responsibilities | Collaborators |
|:---             | :---          |
| Stores event details (time, city, etc.) | |
| Holds ListManager (waitlist, acceptedlist, cancelledlist)| ListManager |
| Links to QR code | QRManager |
| Holds event poster | |

### ListManager
|Responsibilities | Collaborators |
|:---             | :---          |
| Holds and tracks wait list, manages limits | Event |
| Holds and tracks invited list | |
| Holds and tracks accepted list| |
| Holds and tracks cancelled list| |
| Samples Entrants from waiting list to be added to invite list| Organizer |
| Moves entrants between lists | Entrant |
| Triggers a notification | NotificationManager|

### NotificationManager
|Responsibilities | Collaborators |
|:---             | :---          |
| Send non-automated in-app notifications to entrants| ListManager |
| Recieve in-app notifications | Entrant |
| Handle notification preferences |  |

### ImageManager
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle profile pic upload and storage | User |
| Handle event pic upload and storage| Event |

### QRManager
|Responsibilities | Collaborators |
|:---             | :---          |
| Generate QR code for event | Organizer |
| Store hashed data | Event |
| Scan QR code and navigate to event| User / Event |

### Map
|Responsibilities | Collaborators |
|:---             | :---          |
| Display where entrants join the waitlist from | Organizer|
| Store user locations | |

### EventDetailsActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display the event details | Event|
| Allow sign up to event | Event, Entrant |

### EventDetailsController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle event sign-up button click | EventDetailsActivity, Event, Entrant |
| Handle leave waiting list button click | EventDetailsActivity, Event, Entrant |
| Handle delete event button click | EventDetailsActivity, Event, Admin |
| Handle edit event button click | EventDetailsActivity, Event, Organizer |

### UpdateProfileActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display profile details | User |
| Allow profile details to be edited |  User |

### UpdateProfileController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle save changes click | UpdateProfileActivity, Event|
| Handle update profile picture click | MainPageActivity, Event |

### SignUpActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display sign up page | |
| Allow user to sign with profile information |  |
### SignUpController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle sign up click | User, LoginManager |

### WaitListActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display event waitlist | Event, Organizer |
| Display status of entrants in waitlist | Event, Entrant |

### WaitListController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle cancel user click | WaitListActivity, Event, Entrant, Organizer |
| Handle menu button click | WaitListActivity, Organizer |

### SendInviteFragment
|Responsibilities | Collaborators |
|:---             | :---          |
| Interface to send invites to entrants in waitlist | |

### SendInviteController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle send invite | Event, Entrant, Organizer |

### NotificationActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display notifications | Entrant |
| Allow responses to invites |  User |

### NotificationController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle accept invitation click | NotificationActivity, Event, Entrant |
| Handle reject invitation click | NotificationActivity, Event, Entrant |
| Handle claim chosen for lottery | NotificationActivity, Event, Entrant |
| Handle reject chosen for lottery | NotificationActivity, Event, Entrant |

### EditFacilityActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display and edit facility information | Organizer |

### EditFacilityController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle save changes | Organizer |

### EntrantDashboardActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display list of signed up events | Entrant, event |
| Navigation to profile and events |  |

### EntrantDashboardController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle notification toggle | Entrant, NotificationManager |
| Handle profile click | |
| Handle event click | Event |
| Handle event update click | |
| Handle join events click | |

### OrganizerDashboardActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Allow navigation to owned events | |
| Allow navigation to facility information | |

### OrganizerDashboardController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle navigation to owned events | |
| Handle navigation to facility information | |

### MapActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display where entrants joined event | Event, Entrant, Organizer |

### EventBrowseActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display list of events | Event, Admin |

### EventBrowseController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle event click | EventBrowseActivity, Event|
| Handle search bar click | EventBrowseActivity |
| Handle browse profiles | EventBrowseActivity |
| Handle browse events | EventBrowseActivity |

### EventEditActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display event information | Event, Admin |

### EventEditController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle remove event | EventEditActivity, Event |
| Handle remove event image | EventEditActivity, Event |
| Handle remove event QR code | EventEditActivity, Event |

### ProfileBrowseActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display list of profiles | User, Admin |

### ProfileBrowseController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle profile click | ProfileBrowseActivity, User |

### ProfileEditActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Display profile information | User, Admin |

### ProfileEditController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle delete profile | ProfileEditActivity, User |
| Handle delete profile image | ProfileEditActivity, User |

### AddEventActivity
|Responsibilities | Collaborators |
|:---             | :---          |
| Allow creation of new event | Event, Organizer |

### AddEventController
|Responsibilities | Collaborators |
|:---             | :---          |
| Handle event creation | AddEventActivity, Event, Organizer |
