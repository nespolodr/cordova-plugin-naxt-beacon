
#import "AppDelegate+scanservice.h"
#import <objc/runtime.h>

static char beaconManagerKey;

@implementation AppDelegate () <ESTBeaconManagerDelegate>

- (id) getCommandInstance:(NSString*)className
{
    return [self.viewController getCommandInstance:className];
}

// its dangerous to override a method from within a category.
// Instead we will use method swizzling. we set this up in the load call.
+ (void)load
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        Class class = [self class];

        SEL originalSelector = @selector(init);
        SEL swizzledSelector = @selector(scanServiceInit);

        Method original = class_getInstanceMethod(class, originalSelector);
        Method swizzled = class_getInstanceMethod(class, swizzledSelector);

        BOOL didAddMethod =
        class_addMethod(class,
                        originalSelector,
                        method_getImplementation(swizzled),
                        method_getTypeEncoding(swizzled));

        if (didAddMethod) {
            class_replaceMethod(class,
                                swizzledSelector,
                                method_getImplementation(original),
                                method_getTypeEncoding(original));
        } else {
            method_exchangeImplementations(original, swizzled);
        }
    });
}

- (AppDelegate *)scanServiceInit
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(createNotificationChecker:)
                                                 name:UIApplicationDidFinishLaunchingNotification
                                               object:nil];

    // This actually calls the original init method over in AppDelegate. Equivilent to calling super
    // on an overrided method, this is not recursive, although it appears that way. neat huh?
    return [self scanServiceInit];
}

// This code will be called immediately after application:didFinishLaunchingWithOptions:. We need
// to process notifications in cold-start situations
- (void)createNotificationChecker
{

  self.beaconManager = [ESTBeaconManager new];
  self.beaconManager.delegate = self;

  [self.beaconManager requestAlwaysAuthorization];
  [self.beaconManager startMonitoringForRegion:[[CLBeaconRegion alloc]
                                                initWithProximityUUID:[[NSUUID alloc]
                                                                       initWithUUIDString:@"B9407F30-F5F8-466E-AFF9-25556B57FE6D"]
                                                major:23772 minor:39582 identifier:@"monitored region"]];
  [[UIApplication sharedApplication]
   registerUserNotificationSettings:[UIUserNotificationSettings
                                     settingsForTypes:UIUserNotificationTypeAlert
                                     categories:nil]];

}

- (ESTBeaconManager *)beaconManager
{
    return objc_getAssociatedObject(self, &beaconManagerKey);
}

- (void)setBeaconManager:(ESTBeaconManager *)aESTBeaconManager
{
    objc_setAssociatedObject(self, &beaconManagerKey, aESTBeaconManager, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

/**
 * CoreLocation monitoring event.
 */
- (void) beaconManager:(id)manager
     didDetermineState:(CLRegionState)state
             forRegion:(CLBeaconRegion*)region
{

    // Create state string.
    NSString* stateString;
    switch (state)
    {
        case CLRegionStateInside:
            stateString = @"inside";
            break;
        case CLRegionStateOutside:
            stateString = @"outside";
            break;
        case CLRegionStateUnknown:
        default:
            stateString = @"unknown";
    }

    UILocalNotification *notification = [UILocalNotification new];
    notification.alertBody = stateString;
    [[UIApplication sharedApplication] presentLocalNotificationNow:notification];
}

@end
