
#import "AppDelegate+ScanService.h"
#import <objc/runtime.h>

@implementation AppDelegate (ScanService)

void swizzleMethod(Class c, SEL originalSelector)
{
    NSString *original = NSStringFromSelector(originalSelector);

    SEL swizzledSelector = NSSelectorFromString([@"swizzled_" stringByAppendingString:original]);
    SEL noopSelector = NSSelectorFromString([@"noop_" stringByAppendingString:original]);

    Method originalMethod, swizzledMethod, noop;
    originalMethod = class_getInstanceMethod(c, originalSelector);
    swizzledMethod = class_getInstanceMethod(c, swizzledSelector);
    noop = class_getInstanceMethod(c, noopSelector);

    BOOL didAddMethod = class_addMethod(c,
                    originalSelector,
                    method_getImplementation(swizzledMethod),
                    method_getTypeEncoding(swizzledMethod));

    if (didAddMethod)
    {
        class_replaceMethod(c,
                            swizzledSelector,
                            method_getImplementation(noop),
                            method_getTypeEncoding(originalMethod));
    }
    else
    {
        method_exchangeImplementations(originalMethod, swizzledMethod);
    }
}

+ (void)load
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        Class cls = [self class];

        swizzleMethod(cls, @selector(application:didFinishLaunchingWithOptions:));
        //swizzleMethod(cls, @selector(applicationDidBecomeActive:));
    });
}

- (BOOL)swizzled_application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
{
    // Original application is exchanged with swizzled_application
    // So, when calling swizzled_application, we are actually calling the original application method
    // similar with subclass calling super method. Neat!
    BOOL ret = [self swizzled_application:application didFinishLaunchingWithOptions:launchOptions];

    if (ret) {

      ESTBeaconManager *beaconManager;

      beaconManager = [ESTBeaconManager new];

      [beaconManager requestAlwaysAuthorization];
      [beaconManager startMonitoringForRegion:[[CLBeaconRegion alloc]
                                                    initWithProximityUUID:[[NSUUID alloc]
                                                                           initWithUUIDString:@"B9407F30-F5F8-466E-AFF9-25556B57FE6D"]
                                                    major:23772 minor:39582 identifier:@"monitored region"]];
      [[UIApplication sharedApplication]
       registerUserNotificationSettings:[UIUserNotificationSettings
                                         settingsForTypes:UIUserNotificationTypeAlert
                                         categories:nil]];


    }

    return ret;
}

- (BOOL)noop_application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
{
    return YES;
}

//- (void)swizzled_applicationDidBecomeActive:(UIApplication *)application
//{
//    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
//    int num=application.applicationIconBadgeNumber;
//    if(num!=0){
//        AVInstallation *currentInstallation = [AVInstallation currentInstallation];
//        [currentInstallation setBadge:0];
//        [currentInstallation saveEventually];
//        application.applicationIconBadgeNumber=0;
//    }
//}
//
//- (void)noop_applicationDidBecomeActive:(UIApplication *)application
//{}


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
